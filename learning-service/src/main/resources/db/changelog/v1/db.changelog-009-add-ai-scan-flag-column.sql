ALTER TABLE IF EXISTS word_context_templates
ADD IF NOT EXISTS is_selected_by_ai BOOLEAN DEFAULT FALSE;
