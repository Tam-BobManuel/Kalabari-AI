CREATE TABLE IF NOT EXISTS public.datasets (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    file_url VARCHAR(1024) NOT NULL,
    source_language VARCHAR(10),
    target_language VARCHAR(10),
    row_count INTEGER,
    file_size_bytes BIGINT,
    content_type VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::text, NOW())
);

CREATE INDEX idx_datasets_user_id ON public.datasets(user_id);
