# Exercise Submission & Wrong Answer Handling Plan

> **Version:** 1.1  
> **Date:** February 23, 2026  
> **Purpose:** Define API contracts and mobile handling logic for exercise submissions

---

## Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Exercise Flow](#exercise-flow)
3. [API Contracts](#api-contracts)
4. [Wrong Answer Handling](#wrong-answer-handling)
5. [Mobile State Management](#mobile-state-management)
6. [Exercise Type Specifications](#exercise-type-specifications)
7. [Progress & Scoring Logic](#progress--scoring-logic)
8. [Session Pause & Resume](#session-pause--resume)
9. [Error Handling](#error-handling)

---

## Architecture Overview

### Responsibility Split

| Responsibility | Owner | Rationale |
|---------------|-------|-----------|
| Fetch all exercises for lesson | Backend | Single request, reduces latency |
| Navigate between exercises | **Frontend** | Exercises pre-loaded, smoother UX |
| Evaluate answer correctness | Backend | Centralized logic, security |
| Decide retry/skip logic | **Frontend** | Immediate feedback, offline capable |
| Update progress & scores | Backend | Data consistency, anti-cheat |
| Handle wrong answer UI | **Frontend** | Better UX control |

### Data Flow

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         LESSON LIFECYCLE                                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌──────────┐    GET /lessons/{id}/exercises    ┌──────────────────┐   │
│  │  Mobile  │ ────────────────────────────────► │     Backend      │   │
│  │   App    │                                   │                  │   │
│  │          │ ◄──────────────────────────────── │  Return ALL      │   │
│  │          │    List<ExerciseResponse>         │  exercises       │   │
│  └────┬─────┘                                   └──────────────────┘   │
│       │                                                                 │
│       │  Mobile stores exercises locally                               │
│       │  Mobile manages currentExerciseIndex                           │
│       ▼                                                                 │
│  ┌──────────┐                                                          │
│  │ Exercise │◄─────────────────────────────────────────────┐           │
│  │ Screen   │                                              │           │
│  └────┬─────┘                                              │           │
│       │                                                    │           │
│       │ User submits answer                                │           │
│       ▼                                                    │           │
│  ┌──────────┐    POST /exercises/{id}/submit   ┌──────────┴─────────┐ │
│  │  Mobile  │ ───────────────────────────────► │     Backend        │ │
│  │          │                                  │  - Evaluate        │ │
│  │          │ ◄─────────────────────────────── │  - Update progress │ │
│  │          │    SubmissionResult              │  - Return feedback │ │
│  └────┬─────┘                                  └────────────────────┘ │
│       │                                                                │
│       │ Mobile decides: next exercise OR retry OR show correction     │
│       │                                                                │
└───────┴────────────────────────────────────────────────────────────────┘
```

---

## Exercise Flow

### Initial Load Sequence

```
1. Mobile: GET /v1/lessons/{lessonId}
   Response: { lesson metadata, status, totalItems }

2. Mobile: GET /v1/lessons/{lessonId}/exercises
   Response: [
     { id, type, exerciseData, sequenceOrder, isCompleted, isCorrect },
     { id, type, exerciseData, sequenceOrder, isCompleted, isCorrect },
     ...
   ]

3. Mobile: Store exercises in local state
4. Mobile: Find first incomplete exercise (or start from beginning)
5. Mobile: Display exercise[currentIndex]
```

### Answer Submission Sequence

```
1. User interacts with exercise
2. User taps "Submit" / "Check"
3. Mobile: POST /v1/lesson-exercises/{exerciseId}/submit
   Body: { answer, timeSpentSeconds }
4. Backend: Evaluate & return result
5. Mobile: Handle result based on isCorrect
   - If correct: Show success feedback → Move to next
   - If wrong: Show correction → Apply retry logic
```

---

## API Contracts

### Submit Exercise Answer

**Endpoint:** `POST /v1/lesson-exercises/{exerciseId}/submit`

**Request Body:**
```json
{
  "answer": "string | number | string[]",
  "timeSpentSeconds": 45
}
```

**Answer Format by Exercise Type:**

| Exercise Type | Answer Format | Example |
|--------------|---------------|---------|
| CONTEXTUAL_DISCOVERY | `"ACKNOWLEDGED"` | `"ACKNOWLEDGED"` |
| MULTIPLE_CHOICE | `number` (option index) | `0` |
| MECHANIC_DRILL | `string` | `"sustainable"` |
| ASSISTED_COMPOSITION | `string` | `"sustainable"` |
| SENTENCE_BUILDING | `string[]` (ordered) | `["We", "need", "to", "go"]` |
| TARGET_WORD_INTEGRATION | `string` (full sentence) | `"The framework is popular."` |
| CLOZE_WITH_AUDIO | `string` | `"framework"` |

**Response Body:**
```json
{
  "success": true,
  "data": {
    "exerciseId": "uuid",
    "isCorrect": false,
    "score": 0,
    "attemptNumber": 1,
    "maxAttempts": 3,
    
    "feedback": {
      "message": "Not quite right. Try again!",
      "correctAnswer": "sustainable",
      "userAnswer": "substainable",
      "hint": "Check your spelling",
      "explanation": "Sustainable means able to be maintained at a certain level.",
      "alternatives": ["more sustainable", "environmentally sustainable"]
    },
    
    "exerciseState": {
      "isCompleted": false,
      "canRetry": true,
      "attemptsRemaining": 2
    },
    
    "lessonProgress": {
      "completedItems": 4,
      "totalItems": 10,
      "correctItems": 3,
      "accuracyRate": 0.75
    },
    
    "vocabProgress": {
      "wordId": "uuid",
      "wordText": "sustainable",
      "proficiencyScore": 0.45,
      "isLearning": true,
      "isMastered": false
    }
  }
}
```

---

## Wrong Answer Handling

### Strategy Overview

When a user answers incorrectly, the system should **encourage learning** rather than punish mistakes. The approach varies by exercise difficulty level.

### Retry Policy by Exercise Type

| Exercise Type | Max Attempts | Show Answer After | Retry Delay |
|--------------|--------------|-------------------|-------------|
| CONTEXTUAL_DISCOVERY | 1 (auto-pass) | N/A | N/A |
| MULTIPLE_CHOICE | 2 | 2nd wrong | None |
| MECHANIC_DRILL | 3 | 3rd wrong | None |
| ASSISTED_COMPOSITION | 3 | 3rd wrong | None |
| SENTENCE_BUILDING | 3 | 3rd wrong | None |
| TARGET_WORD_INTEGRATION | 2 | 2nd wrong | None |
| CLOZE_WITH_AUDIO | 3 | 3rd wrong | Allow replay |

### Wrong Answer Scenarios

#### Scenario 1: First Wrong Attempt (Retriable)

```
┌─────────────────────────────────────────────────────────────┐
│  User submits wrong answer (Attempt 1 of 3)                 │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Backend Response:                                          │
│  {                                                          │
│    "isCorrect": false,                                      │
│    "attemptNumber": 1,                                      │
│    "maxAttempts": 3,                                        │
│    "exerciseState": {                                       │
│      "isCompleted": false,                                  │
│      "canRetry": true,                                      │
│      "attemptsRemaining": 2                                 │
│    },                                                       │
│    "feedback": {                                            │
│      "message": "Not quite! Here's a hint...",              │
│      "hint": "Think about environmental context",           │
│      "correctAnswer": null  // NOT revealed yet             │
│    }                                                        │
│  }                                                          │
│                                                             │
│  Mobile Actions:                                            │
│  1. Show feedback message with hint                         │
│  2. Highlight wrong answer (red shake animation)            │
│  3. Clear input field OR keep for reference                 │
│  4. Show "Try Again" button                                 │
│  5. Optionally show "Skip" button                           │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

#### Scenario 2: Second Wrong Attempt (Last Chance)

```
┌─────────────────────────────────────────────────────────────┐
│  User submits wrong answer (Attempt 2 of 3)                 │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Backend Response:                                          │
│  {                                                          │
│    "isCorrect": false,                                      │
│    "attemptNumber": 2,                                      │
│    "exerciseState": {                                       │
│      "canRetry": true,                                      │
│      "attemptsRemaining": 1                                 │
│    },                                                       │
│    "feedback": {                                            │
│      "message": "One more try! The answer starts with 's'", │
│      "hint": "sus_ _ _ _ _ _ _ _e"  // Progressive hint     │
│    }                                                        │
│  }                                                          │
│                                                             │
│  Mobile Actions:                                            │
│  1. Show stronger hint                                      │
│  2. Display "Last attempt!" warning                         │
│  3. Maybe show partial answer (first letter)                │
│  4. Encourage with supportive message                       │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

#### Scenario 3: Final Wrong Attempt (Reveal Answer)

```
┌─────────────────────────────────────────────────────────────┐
│  User submits wrong answer (Attempt 3 of 3 - FINAL)         │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Backend Response:                                          │
│  {                                                          │
│    "isCorrect": false,                                      │
│    "attemptNumber": 3,                                      │
│    "exerciseState": {                                       │
│      "isCompleted": true,  // Marked complete (failed)      │
│      "canRetry": false,                                     │
│      "attemptsRemaining": 0                                 │
│    },                                                       │
│    "feedback": {                                            │
│      "message": "The correct answer is:",                   │
│      "correctAnswer": "sustainable",                        │
│      "explanation": "Sustainable means...",                 │
│      "alternatives": ["more sustainable"]                   │
│    }                                                        │
│  }                                                          │
│                                                             │
│  Mobile Actions:                                            │
│  1. Show correct answer prominently (green highlight)       │
│  2. Display explanation                                     │
│  3. Play audio pronunciation (if available)                 │
│  4. Show "Got it" / "Continue" button                       │
│  5. Mark exercise as reviewed (not mastered)                │
│  6. Schedule word for additional practice later             │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

#### Scenario 4: User Skips Exercise

```
┌─────────────────────────────────────────────────────────────┐
│  User taps "Skip" without answering                         │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Mobile Actions (No API call needed):                       │
│  1. Show confirmation: "Skip this exercise?"                │
│  2. If confirmed:                                           │
│     - Mark locally as skipped                               │
│     - Move to next exercise                                 │
│     - Add to "skipped" list for end-of-lesson review        │
│  3. At lesson end, prompt to retry skipped exercises        │
│                                                             │
│  OR (with API call):                                        │
│  POST /v1/lesson-exercises/{id}/skip                        │
│  Response: { skipped: true, canRetryLater: true }           │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Progressive Hint System

For exercises that support hints, reveal more information with each wrong attempt:

```
Attempt 1: Generic hint
  → "Think about the environment"

Attempt 2: Structural hint  
  → "It starts with 'sus' and ends with 'able'"

Attempt 3: Reveal answer
  → "sustainable"
```

---

## Mobile State Management

### Local State Structure

```typescript
interface LessonState {
  lessonId: string;
  status: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED';
  
  exercises: Exercise[];
  currentExerciseIndex: number;
  
  // Track attempts per exercise
  attemptTracker: Map<string, {
    attempts: number;
    answers: string[];
    isCompleted: boolean;
    isCorrect: boolean | null;
  }>;
  
  // For end-of-lesson review
  wrongAnswers: Exercise[];
  skippedExercises: Exercise[];
  
  // Progress
  completedCount: number;
  correctCount: number;
}
```

### State Transitions

```
┌─────────────────────────────────────────────────────────────────────┐
│                    MOBILE STATE MACHINE                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│                        ┌───────────────┐                            │
│                        │ LESSON_LOADED │                            │
│                        └───────┬───────┘                            │
│                                │                                    │
│                                ▼                                    │
│                     ┌────────────────────┐                          │
│         ┌──────────►│ SHOWING_EXERCISE   │◄──────────┐              │
│         │           └─────────┬──────────┘           │              │
│         │                     │                      │              │
│         │                     │ onSubmit()           │              │
│         │                     ▼                      │              │
│         │           ┌────────────────────┐           │              │
│         │           │ AWAITING_RESULT    │           │              │
│         │           └─────────┬──────────┘           │              │
│         │                     │                      │              │
│         │        ┌────────────┴────────────┐         │              │
│         │        ▼                         ▼         │              │
│    ┌────────────────┐              ┌────────────────┐│              │
│    │ SHOW_CORRECT   │              │ SHOW_WRONG     ││              │
│    │ _FEEDBACK      │              │ _FEEDBACK      ││              │
│    └───────┬────────┘              └───────┬────────┘│              │
│            │                               │         │              │
│            │                      ┌────────┴───────┐ │              │
│            │                      ▼                ▼ │              │
│            │              ┌─────────────┐  ┌─────────┴───┐          │
│            │              │ CAN_RETRY   │  │ MAX_ATTEMPTS │          │
│            │              │ (attempts<3)│  │ _REACHED     │          │
│            │              └──────┬──────┘  └──────┬──────┘          │
│            │                     │                │                 │
│            │                     │ onRetry()      │                 │
│            │                     │                │                 │
│            │           ┌─────────┘                │                 │
│            │           │                         │                 │
│            │           │         ┌───────────────┘                 │
│            ▼           ▼         ▼                                  │
│    ┌─────────────────────────────────┐                             │
│    │       MOVE_TO_NEXT              │                             │
│    │  currentIndex++                 │                             │
│    │  if (index >= total) → COMPLETE │                             │
│    └─────────────────────────────────┘                             │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### Mobile Handler Pseudocode

```typescript
class ExerciseHandler {
  
  async submitAnswer(exerciseId: string, answer: any): Promise<void> {
    this.setState('AWAITING_RESULT');
    
    try {
      const result = await api.submitExercise(exerciseId, {
        answer,
        timeSpentSeconds: this.getTimeSpent()
      });
      
      // Update local attempt tracker
      this.attemptTracker.set(exerciseId, {
        attempts: result.attemptNumber,
        isCompleted: result.exerciseState.isCompleted,
        isCorrect: result.isCorrect
      });
      
      if (result.isCorrect) {
        this.handleCorrectAnswer(result);
      } else {
        this.handleWrongAnswer(result);
      }
      
    } catch (error) {
      this.handleNetworkError(error);
    }
  }
  
  handleCorrectAnswer(result: SubmissionResult): void {
    // 1. Show success animation
    this.showSuccessFeedback(result.feedback.message);
    
    // 2. Update progress
    this.correctCount++;
    this.completedCount++;
    
    // 3. After delay, move to next
    setTimeout(() => this.moveToNextExercise(), 1500);
  }
  
  handleWrongAnswer(result: SubmissionResult): void {
    const { exerciseState, feedback } = result;
    
    // 1. Show wrong animation
    this.showWrongFeedback(feedback.message);
    
    if (exerciseState.canRetry) {
      // 2a. Still has attempts - show retry option
      this.setState('CAN_RETRY');
      this.showHint(feedback.hint);
      this.showRetryButton(exerciseState.attemptsRemaining);
      
    } else {
      // 2b. No more attempts - reveal answer
      this.setState('MAX_ATTEMPTS_REACHED');
      this.revealCorrectAnswer(feedback.correctAnswer, feedback.explanation);
      this.addToWrongAnswersList(this.currentExercise);
      this.completedCount++;
      
      // 3. Show continue button
      this.showContinueButton();
    }
  }
  
  moveToNextExercise(): void {
    this.currentExerciseIndex++;
    
    if (this.currentExerciseIndex >= this.exercises.length) {
      this.handleLessonComplete();
    } else {
      this.setState('SHOWING_EXERCISE');
      this.displayExercise(this.exercises[this.currentExerciseIndex]);
    }
  }
  
  handleLessonComplete(): void {
    // Check if there are wrong/skipped exercises to review
    if (this.wrongAnswers.length > 0 || this.skippedExercises.length > 0) {
      this.showReviewPrompt();
    } else {
      this.showLessonComplete();
    }
  }
  
  retryExercise(): void {
    // Clear previous answer, keep on same exercise
    this.clearAnswerInput();
    this.setState('SHOWING_EXERCISE');
  }
  
  skipExercise(): void {
    this.skippedExercises.push(this.currentExercise);
    this.moveToNextExercise();
  }
}
```

---

## Exercise Type Specifications

### CONTEXTUAL_DISCOVERY (Passive)
```
No wrong answer possible - auto-completes after user acknowledges.
Used for: First exposure to a word in context.
```

### MULTIPLE_CHOICE
```
Wrong Handling:
- Attempt 1: Highlight wrong option red, show hint
- Attempt 2: Reveal correct answer, explain why

UI Behavior:
- Disable selected wrong option
- Allow selecting different option
```

### MECHANIC_DRILL
```
Wrong Handling:
- Attempt 1: "Not quite" + grammatical hint
- Attempt 2: Show first letter
- Attempt 3: Reveal full answer

Fuzzy Matching:
- Ignore case: "Sustainable" = "sustainable" ✓
- Trim whitespace: " sustainable " = "sustainable" ✓
- Typo tolerance (optional): "sustainabel" → suggest correction
```

### ASSISTED_COMPOSITION
```
Wrong Handling:
- Check against expectedWord AND alternativeAnswers
- Attempt 1: Generic hint from exercise data
- Attempt 2: "The word starts with..."
- Attempt 3: Reveal + show all alternatives

Special: If answer is close (1-2 char diff), suggest "Did you mean...?"
```

### SENTENCE_BUILDING
```
Wrong Handling:
- Compare word by word
- Highlight which blocks are in wrong position
- Attempt 1: Show first word position
- Attempt 2: Show first 2-3 words
- Attempt 3: Reveal full correct sentence

UI: Allow drag-drop rearrangement, shake wrong blocks
```

### TARGET_WORD_INTEGRATION (AI-Evaluated)
```
Evaluation Criteria:
1. Contains target word ✓
2. Word count within range ✓
3. Grammatically correct (AI)
4. Makes semantic sense (AI)

Wrong Handling:
- If missing target word: "Make sure to use the word 'X'"
- If too short/long: "Your sentence should be 5-15 words"
- If grammar error: AI provides specific correction
- Attempt 2: Show example response

Note: More lenient scoring - partial credit possible
```

### CLOZE_WITH_AUDIO
```
Wrong Handling:
- Allow audio replay between attempts
- Attempt 1: "Listen again carefully"
- Attempt 2: Show first letter + phonetic hint
- Attempt 3: Reveal word + replay audio

UI: Prominent replay button, maybe slow-down option
```

---

## Progress & Scoring Logic

### Scoring Formula

```
Exercise Score = Base Score × Attempt Multiplier × Time Bonus

Where:
- Base Score = 100
- Attempt Multiplier:
  - 1st attempt correct: 1.0 (100%)
  - 2nd attempt correct: 0.7 (70%)
  - 3rd attempt correct: 0.4 (40%)
  - Failed all attempts: 0.0 (0%)
- Time Bonus (optional):
  - Under 10s: 1.1
  - 10-30s: 1.0
  - Over 30s: 0.9
```

### Vocabulary Proficiency Update

```
After CORRECT answer:
  proficiency += 0.1 × attemptMultiplier
  
After WRONG answer (all attempts):
  proficiency -= 0.05
  
Mastery threshold: proficiency >= 0.9 after 5+ exercises
```

### Lesson Accuracy Calculation

```
accuracyRate = correctCount / completedCount

Where:
- correctCount = exercises answered correctly (any attempt)
- completedCount = all attempted exercises (including failed)
```

---

## End-of-Lesson Review

### Review Flow for Wrong Answers

```
┌─────────────────────────────────────────────────────────────┐
│              LESSON COMPLETE SCREEN                         │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  🎉 Lesson Complete!                                        │
│                                                             │
│  Accuracy: 80% (8/10 correct)                              │
│  Time: 5:32                                                 │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ You struggled with these words:                     │   │
│  │                                                     │   │
│  │ • sustainable - 2 wrong attempts                    │   │
│  │ • framework - 3 wrong attempts (failed)             │   │
│  │                                                     │   │
│  │ [Review These Words]  [Continue to Next Lesson]     │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Review Mode Options

1. **Quick Review**: Show flashcard-style word + meaning + example
2. **Practice Again**: Re-do failed exercises with fresh attempts
3. **Skip for Now**: Words scheduled for next lesson automatically

---

## Session Pause & Resume

### Overview

Users may quit a lesson halfway due to:
- App backgrounded / closed
- Phone call interruption
- Loss of internet connection
- User intentionally pausing to continue later
- Device battery died

The system must gracefully handle all these scenarios and allow seamless resume.

### Key Principle: Server is Source of Truth

```
┌────────────────────────────────────────────────────────────────────────┐
│  Progress is persisted on EACH exercise submission.                   │
│  No explicit "save" action needed.                                    │
│  Resume = Re-fetch lesson data + find first incomplete exercise.      │
└────────────────────────────────────────────────────────────────────────┘
```

---

### Session Lifecycle States

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      LESSON SESSION STATES                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌──────────────┐                                                       │
│  │ NOT_STARTED  │  User hasn't begun this lesson yet                   │
│  └──────┬───────┘                                                       │
│         │ User taps "Start Lesson"                                      │
│         ▼                                                               │
│  ┌──────────────┐                                                       │
│  │ IN_PROGRESS  │  User is actively doing exercises                    │
│  │              │  - startedAt is set                                  │
│  │              │  - completedItems < totalItems                       │
│  └──────┬───────┘                                                       │
│         │                                                               │
│    ┌────┴────┐                                                          │
│    ▼         ▼                                                          │
│ [PAUSED]  [COMPLETED]                                                   │
│    │         │                                                          │
│    │         └──► All exercises done, completedAt is set               │
│    │                                                                    │
│    └──► User quit halfway (implicit state, no DB change)               │
│         Lesson remains IN_PROGRESS with partial completedItems         │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

**Note:** There's no explicit "PAUSED" status in the database. A lesson is considered paused when:
- `status = IN_PROGRESS`
- `completedItems < totalItems`
- User is not currently in the lesson screen

---

### Quit Scenarios & Handling

#### Scenario 1: User Explicitly Quits (Taps Back/Exit)

```
┌─────────────────────────────────────────────────────────────────────────┐
│  User taps "Back" or "Exit" button during lesson                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Mobile Actions:                                                        │
│                                                                         │
│  1. Show confirmation dialog:                                           │
│     ┌─────────────────────────────────────────────┐                    │
│     │  ⚠️ Leave Lesson?                            │                    │
│     │                                             │                    │
│     │  Your progress is saved automatically.      │                    │
│     │  You've completed 4 of 10 exercises.        │                    │
│     │                                             │                    │
│     │  [Continue Learning]  [Leave & Save]        │                    │
│     └─────────────────────────────────────────────┘                    │
│                                                                         │
│  2. If user confirms exit:                                              │
│     - Sync any pending offline submissions                             │
│     - Clear local lesson cache (optional)                              │
│     - Navigate back to lesson list                                     │
│                                                                         │
│  3. No explicit API call needed to "pause"                             │
│     - Progress already saved per-exercise submission                   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

#### Scenario 2: App Backgrounded / Killed

```
┌─────────────────────────────────────────────────────────────────────────┐
│  App goes to background or is killed by OS                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  On App Background (onPause / applicationDidEnterBackground):           │
│                                                                         │
│  1. Save current exercise state to local storage:                       │
│     {                                                                   │
│       lessonId: "uuid",                                                 │
│       currentExerciseIndex: 4,                                          │
│       currentExerciseStartTime: timestamp,                              │
│       partialAnswer: "sustain..."  // if user was typing                │
│     }                                                                   │
│                                                                         │
│  2. If there's a pending answer submission:                            │
│     - Queue it for sync when app resumes                               │
│                                                                         │
│  On App Foreground (onResume / applicationWillEnterForeground):         │
│                                                                         │
│  1. Check if returning to same lesson session                          │
│  2. If < 30 minutes passed:                                            │
│     - Restore from local state                                         │
│     - Continue from same exercise                                      │
│     - Restore partial answer if any                                    │
│  3. If > 30 minutes passed:                                            │
│     - Fetch fresh lesson data from server                              │
│     - Resume from server's last completed exercise + 1                 │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

#### Scenario 3: Network Disconnection Mid-Lesson

```
┌─────────────────────────────────────────────────────────────────────────┐
│  Network lost while user is doing exercises                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Immediate Actions:                                                     │
│  1. Show subtle offline indicator (don't interrupt)                    │
│  2. Continue allowing exercise interactions                            │
│  3. Queue submissions locally                                          │
│                                                                         │
│  On Each Answer Submission (Offline):                                   │
│  1. Evaluate locally if possible (multiple choice, exact match)        │
│  2. Store in offline queue:                                            │
│     offlineQueue.push({                                                │
│       exerciseId,                                                       │
│       answer,                                                           │
│       timeSpentSeconds,                                                 │
│       timestamp,                                                        │
│       localEvaluationResult  // for optimistic UI                      │
│     })                                                                  │
│  3. Move to next exercise optimistically                               │
│  4. Mark with "pending sync" indicator                                 │
│                                                                         │
│  On Network Restore:                                                    │
│  1. Sync all queued submissions in order                               │
│  2. Reconcile any differences (server wins on conflicts)               │
│  3. Update UI if server evaluation differs from local                  │
│  4. Show toast: "Progress synced ✓"                                    │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

### Resume Flow

#### API: Get Lesson with Progress

**Endpoint:** `GET /v1/lessons/{lessonId}`

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "lesson-uuid",
    "userId": "user-uuid",
    "status": "IN_PROGRESS",
    "startedAt": "2026-02-23T10:00:00Z",
    "completedAt": null,
    "totalItems": 10,
    "completedItems": 4,
    "accuracyRate": 0.75,
    
    "resumeInfo": {
      "lastActivityAt": "2026-02-23T10:15:00Z",
      "nextExerciseIndex": 4,
      "nextExerciseId": "exercise-uuid-5",
      "canResume": true,
      "minutesSinceLastActivity": 45
    }
  }
}
```

#### API: Get Exercises with Completion Status

**Endpoint:** `GET /v1/lessons/{lessonId}/exercises`

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": "exercise-1",
      "type": "CONTEXTUAL_DISCOVERY",
      "sequenceOrder": 1,
      "isCompleted": true,
      "isCorrect": true,
      "userAnswer": "ACKNOWLEDGED",
      "attemptCount": 1,
      "exerciseData": { ... }
    },
    {
      "id": "exercise-2",
      "type": "MULTIPLE_CHOICE",
      "sequenceOrder": 2,
      "isCompleted": true,
      "isCorrect": false,
      "userAnswer": "2",
      "attemptCount": 3,
      "exerciseData": { ... }
    },
    {
      "id": "exercise-5",
      "type": "ASSISTED_COMPOSITION",
      "sequenceOrder": 5,
      "isCompleted": false,      // <-- Resume from here
      "isCorrect": null,
      "userAnswer": null,
      "attemptCount": 0,
      "exerciseData": { ... }
    },
    // ... more exercises
  ]
}
```

---

### Mobile Resume Logic

```typescript
class LessonResumeHandler {
  
  async resumeLesson(lessonId: string): Promise<void> {
    // 1. Fetch lesson metadata
    const lesson = await api.getLesson(lessonId);
    
    // 2. Check if lesson can be resumed
    if (lesson.status === 'COMPLETED') {
      this.showLessonCompletedScreen(lesson);
      return;
    }
    
    // 3. Fetch all exercises with their completion status
    const exercises = await api.getLessonExercises(lessonId);
    
    // 4. Find resume point
    const resumeIndex = this.findResumeIndex(exercises);
    
    // 5. Initialize lesson state
    this.initializeLessonState({
      lessonId,
      exercises,
      currentExerciseIndex: resumeIndex,
      completedCount: lesson.completedItems,
      correctCount: this.countCorrect(exercises)
    });
    
    // 6. Show resume UI
    if (lesson.completedItems > 0) {
      this.showResumePrompt(lesson, resumeIndex);
    } else {
      this.startFromBeginning();
    }
  }
  
  findResumeIndex(exercises: Exercise[]): number {
    // Find first incomplete exercise
    const firstIncomplete = exercises.findIndex(ex => !ex.isCompleted);
    
    if (firstIncomplete === -1) {
      // All complete - shouldn't happen if status is IN_PROGRESS
      return exercises.length - 1;
    }
    
    return firstIncomplete;
  }
  
  showResumePrompt(lesson: Lesson, resumeIndex: number): void {
    // Show UI like:
    // ┌─────────────────────────────────────────────┐
    // │  📚 Welcome Back!                           │
    // │                                             │
    // │  You completed 4 of 10 exercises            │
    // │  in this lesson.                            │
    // │                                             │
    // │  [Continue from Exercise 5]                 │
    // │  [Start Over]                               │
    // └─────────────────────────────────────────────┘
    
    this.showDialog({
      title: 'Welcome Back!',
      message: `You completed ${lesson.completedItems} of ${lesson.totalItems} exercises.`,
      primaryAction: {
        label: `Continue from Exercise ${resumeIndex + 1}`,
        onPress: () => this.continueFromIndex(resumeIndex)
      },
      secondaryAction: {
        label: 'Start Over',
        onPress: () => this.confirmStartOver()
      }
    });
  }
  
  confirmStartOver(): void {
    this.showDialog({
      title: 'Start Over?',
      message: 'This will reset your progress for this lesson. Your scores will be recalculated.',
      primaryAction: {
        label: 'Yes, Start Over',
        onPress: () => this.resetLesson()
      },
      secondaryAction: {
        label: 'Cancel',
        onPress: () => this.showResumePrompt()
      }
    });
  }
  
  async resetLesson(): Promise<void> {
    // Call API to reset lesson progress
    await api.resetLesson(this.lessonId);
    
    // Re-fetch fresh data
    await this.resumeLesson(this.lessonId);
  }
}
```

---

### Reset Lesson API (Optional)

If "Start Over" feature is needed:

**Endpoint:** `POST /v1/lessons/{lessonId}/reset`

**Request Body:**
```json
{
  "resetType": "FULL",  // or "WRONG_ONLY" to retry only wrong answers
  "reason": "USER_REQUESTED"  // for analytics
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "lessonId": "uuid",
    "status": "NOT_STARTED",
    "completedItems": 0,
    "totalItems": 10,
    "exercisesReset": 10
  }
}
```

**Backend Actions:**
```java
@Transactional
public LessonResetResponse resetLesson(UUID lessonId, ResetType resetType) {
    DynamicLesson lesson = lessonRepository.findById(lessonId);
    
    if (resetType == ResetType.FULL) {
        // Reset all exercises
        lesson.getExercises().forEach(ex -> {
            ex.setIsCompleted(false);
            ex.setIsCorrect(null);
            ex.setUserAnswer(null);
            ex.setAttemptCount(0);
            ex.setTimeSpentSeconds(null);
        });
        
        lesson.setStatus(LessonStatus.NOT_STARTED);
        lesson.setCompletedItems(0);
        lesson.setAccuracyRate(null);
        lesson.setStartedAt(null);
        
    } else if (resetType == ResetType.WRONG_ONLY) {
        // Reset only wrong/incomplete exercises
        lesson.getExercises().stream()
            .filter(ex -> !Boolean.TRUE.equals(ex.getIsCorrect()))
            .forEach(ex -> {
                ex.setIsCompleted(false);
                ex.setIsCorrect(null);
                ex.setUserAnswer(null);
                ex.setAttemptCount(0);
            });
        
        // Recalculate completed items
        long completed = lesson.getExercises().stream()
            .filter(ex -> Boolean.TRUE.equals(ex.getIsCompleted()))
            .count();
        lesson.setCompletedItems((int) completed);
    }
    
    return buildResetResponse(lesson);
}
```

---

### Local Storage Schema (Mobile)

```typescript
// Key: "lesson_session_{lessonId}"
interface LessonSessionCache {
  lessonId: string;
  fetchedAt: number;           // timestamp when exercises were fetched
  exercises: Exercise[];       // cached exercise data
  
  // Current session state
  currentExerciseIndex: number;
  sessionStartedAt: number;    // when user started this session
  
  // For restoration after app kill
  partialState?: {
    exerciseId: string;
    partialAnswer: any;        // what user was typing
    elapsedSeconds: number;    // time spent on current exercise
  };
  
  // Offline queue
  pendingSubmissions: PendingSubmission[];
}

interface PendingSubmission {
  exerciseId: string;
  answer: any;
  timeSpentSeconds: number;
  submittedAt: number;
  localResult?: {              // optimistic evaluation
    isCorrect: boolean;
    feedback: string;
  };
  syncStatus: 'PENDING' | 'SYNCING' | 'FAILED';
}
```

---

### Session Timeout Handling

```typescript
const SESSION_TIMEOUT_MINUTES = 30;

class SessionManager {
  
  checkSessionValidity(cachedSession: LessonSessionCache): SessionStatus {
    const minutesElapsed = (Date.now() - cachedSession.fetchedAt) / 60000;
    
    if (minutesElapsed > SESSION_TIMEOUT_MINUTES) {
      return {
        isValid: false,
        reason: 'SESSION_EXPIRED',
        action: 'REFETCH_FROM_SERVER'
      };
    }
    
    if (cachedSession.pendingSubmissions.length > 0) {
      return {
        isValid: true,
        reason: 'HAS_PENDING_SYNC',
        action: 'SYNC_THEN_CONTINUE'
      };
    }
    
    return {
      isValid: true,
      reason: 'VALID',
      action: 'CONTINUE_FROM_CACHE'
    };
  }
  
  async handleSessionResume(lessonId: string): Promise<void> {
    const cached = await storage.get(`lesson_session_${lessonId}`);
    
    if (!cached) {
      // No cache - fresh start
      await this.loadLessonFromServer(lessonId);
      return;
    }
    
    const status = this.checkSessionValidity(cached);
    
    switch (status.action) {
      case 'REFETCH_FROM_SERVER':
        // Cache too old, get fresh data
        await this.loadLessonFromServer(lessonId);
        break;
        
      case 'SYNC_THEN_CONTINUE':
        // Sync pending submissions first
        await this.syncPendingSubmissions(cached.pendingSubmissions);
        // Then refresh from server to get accurate state
        await this.loadLessonFromServer(lessonId);
        break;
        
      case 'CONTINUE_FROM_CACHE':
        // Use cached data, but validate with quick server check
        await this.validateAndContinue(cached);
        break;
    }
  }
  
  async validateAndContinue(cached: LessonSessionCache): Promise<void> {
    // Quick validation - compare server completedItems with local
    const serverLesson = await api.getLesson(cached.lessonId);
    
    const localCompleted = cached.exercises.filter(e => e.isCompleted).length;
    
    if (serverLesson.completedItems !== localCompleted) {
      // Mismatch - server wins, refetch
      console.warn('Local/server mismatch, refetching');
      await this.loadLessonFromServer(cached.lessonId);
    } else {
      // Match - continue from cache
      this.restoreFromCache(cached);
    }
  }
}
```

---

### Edge Cases & Solutions

| Edge Case | Solution |
|-----------|----------|
| User submits, app crashes before response | Pending in local queue, sync on resume |
| User does exercise offline, later online shows different server state | Server wins, notify user of reconciliation |
| User starts lesson on Device A, continues on Device B | Device B fetches from server, gets Device A's progress |
| User tries to reset a completed lesson | Allow reset, but warn scores may change |
| Exercise data changed while user was away (admin update) | Re-fetch exercises, handle gracefully if structure changed |
| User has poor connection (responses timeout) | Implement retry with exponential backoff |

---

### Flow Diagram: Complete Resume Scenario

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    USER RETURNS TO LESSON                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  User opens app → Taps on "Continue Lesson"                            │
│         │                                                               │
│         ▼                                                               │
│  ┌─────────────────┐                                                   │
│  │ Check Local     │                                                   │
│  │ Cache Exists?   │                                                   │
│  └────────┬────────┘                                                   │
│           │                                                             │
│     ┌─────┴─────┐                                                       │
│     ▼           ▼                                                       │
│   [YES]       [NO]                                                      │
│     │           │                                                       │
│     │           └──► Fetch from server ──► Show exercises              │
│     │                                                                   │
│     ▼                                                                   │
│  ┌─────────────────┐                                                   │
│  │ Cache < 30 min? │                                                   │
│  └────────┬────────┘                                                   │
│           │                                                             │
│     ┌─────┴─────┐                                                       │
│     ▼           ▼                                                       │
│   [YES]       [NO]                                                      │
│     │           │                                                       │
│     │           └──► Fetch fresh from server                           │
│     │                                                                   │
│     ▼                                                                   │
│  ┌─────────────────┐                                                   │
│  │ Has Pending     │                                                   │
│  │ Submissions?    │                                                   │
│  └────────┬────────┘                                                   │
│           │                                                             │
│     ┌─────┴─────┐                                                       │
│     ▼           ▼                                                       │
│   [YES]       [NO]                                                      │
│     │           │                                                       │
│     │           └──► Use cache directly                                │
│     │                     │                                             │
│     ▼                     │                                             │
│  ┌─────────────────┐      │                                             │
│  │ Sync Pending    │      │                                             │
│  │ Submissions     │      │                                             │
│  └────────┬────────┘      │                                             │
│           │               │                                             │
│           ▼               ▼                                             │
│  ┌─────────────────────────────────────┐                               │
│  │ Show Resume Dialog                  │                               │
│  │ "Continue from Exercise X?"         │                               │
│  └────────────────┬────────────────────┘                               │
│                   │                                                     │
│          ┌───────┴───────┐                                             │
│          ▼               ▼                                             │
│    [Continue]      [Start Over]                                        │
│          │               │                                             │
│          │               └──► POST /lessons/{id}/reset                 │
│          │                         │                                   │
│          ▼                         ▼                                   │
│  ┌─────────────────────────────────────┐                               │
│  │ Display Exercise[resumeIndex]       │                               │
│  └─────────────────────────────────────┘                               │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Error Handling

### Network Errors

```typescript
handleNetworkError(error: Error): void {
  if (isOffline()) {
    // Queue submission for later
    this.offlineQueue.push({
      exerciseId: this.currentExercise.id,
      answer: this.currentAnswer,
      timestamp: Date.now()
    });
    
    // Optimistically move forward (mark as pending sync)
    this.showOfflineToast("Answer saved. Will sync when online.");
    this.moveToNextExercise();
    
  } else {
    // Show retry option
    this.showErrorToast("Failed to submit. Tap to retry.");
    this.setState('SUBMISSION_ERROR');
  }
}
```

### Sync on Reconnect

```typescript
onNetworkReconnect(): void {
  for (const queued of this.offlineQueue) {
    await this.submitAnswer(queued.exerciseId, queued.answer);
  }
  this.offlineQueue.clear();
  this.showToast("Progress synced!");
}
```

---

## Summary

| Aspect | Implementation |
|--------|---------------|
| Next exercise navigation | **Frontend** (pre-loaded list) |
| Answer evaluation | **Backend** (API call) |
| Retry logic | **Frontend** (based on API response) |
| Attempt tracking | **Both** (local + server) |
| Wrong answer feedback | **Backend** provides, **Frontend** displays |
| Progress calculation | **Backend** (source of truth) |
| Offline support | **Frontend** queue + sync |
| Session persistence | **Backend** (per-submission save) |
| Resume point detection | **Backend** provides, **Frontend** displays |
| Local session cache | **Frontend** (30-min validity) |
| Lesson reset | **Backend** (optional API) |

---

## Next Steps

### Phase 1: Core Submission Flow
1. [ ] Implement `ExerciseSubmitRequest` DTO
2. [ ] Implement `ExerciseSubmitResponse` DTO  
3. [ ] Create `ExerciseEvaluationService` with strategy pattern
4. [ ] Add `/submit` endpoint to `LessonExerciseController`
5. [ ] Implement type-specific evaluators
6. [ ] Add attempt tracking to `LessonExercise` entity
7. [ ] Create database migration for attempt fields
8. [ ] Write unit tests for evaluation logic

### Phase 2: Session Management
9. [ ] Add `resumeInfo` to lesson response DTO
10. [ ] Implement `GET /lessons/{id}` with progress summary
11. [ ] Implement `POST /lessons/{id}/reset` endpoint (optional)
12. [ ] Add `lastActivityAt` field to `DynamicLesson` entity
13. [ ] Document mobile local storage schema for caching

### Phase 3: Mobile Integration Support
14. [ ] Create comprehensive API documentation (OpenAPI/Swagger)
15. [ ] Add WebSocket support for real-time progress sync (optional)
16. [ ] Implement conflict resolution for multi-device scenarios
