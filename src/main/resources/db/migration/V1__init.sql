--
-- V1__init.sql — Initial schema for personal_blog_db\
--

-- 1. users
CREATE TABLE IF NOT EXISTS users (
    id         BIGINT                    NOT NULL AUTO_INCREMENT,
    username   VARCHAR(100)              NOT NULL,
    email      VARCHAR(255)              NOT NULL,
    password   VARCHAR(255)              NOT NULL,
    role       ENUM('ADMIN', 'READER')   NOT NULL DEFAULT 'READER',
    created_at DATETIME                  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME                  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_users           PRIMARY KEY (id),
    CONSTRAINT uq_users_email     UNIQUE (email),
    CONSTRAINT uq_users_username  UNIQUE (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. categories
CREATE TABLE IF NOT EXISTS categories (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    slug        VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_categories      PRIMARY KEY (id),
    CONSTRAINT uq_categories_slug UNIQUE (slug),
    CONSTRAINT uq_categories_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. posts  (depends on users)
CREATE TABLE IF NOT EXISTS posts (
    id              BIGINT                       NOT NULL AUTO_INCREMENT,
    title           VARCHAR(500)                 NOT NULL,
    slug            VARCHAR(255)                 NOT NULL,
    summary         TEXT,
    content         LONGTEXT                     NOT NULL,
    cover_image_url VARCHAR(500),
    status          ENUM('DRAFT', 'PUBLISHED')   NOT NULL DEFAULT 'DRAFT',
    author_id       BIGINT                       NOT NULL,
    published_at    DATETIME,
    created_at      DATETIME                     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_posts        PRIMARY KEY (id),
    CONSTRAINT uq_posts_slug   UNIQUE (slug),
    CONSTRAINT fk_posts_author FOREIGN KEY (author_id)
        REFERENCES users (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_posts_status       ON posts (status);
CREATE INDEX idx_posts_author_id    ON posts (author_id);
CREATE INDEX idx_posts_published_at ON posts (published_at);

-- 4. tags
CREATE TABLE IF NOT EXISTS tags (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL,
    slug       VARCHAR(100) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_tags      PRIMARY KEY (id),
    CONSTRAINT uq_tags_slug UNIQUE (slug),
    CONSTRAINT uq_tags_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. post_categories  (join table: posts <-> categories)
CREATE TABLE IF NOT EXISTS post_categories (
    post_id     BIGINT NOT NULL,
    category_id BIGINT NOT NULL,

    CONSTRAINT pk_post_categories PRIMARY KEY (post_id, category_id),
    CONSTRAINT fk_pc_post         FOREIGN KEY (post_id)     REFERENCES posts(id)      ON DELETE CASCADE,
    CONSTRAINT fk_pc_category     FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. post_tags  (join table: posts <-> tags)
CREATE TABLE IF NOT EXISTS post_tags (
    post_id BIGINT NOT NULL,
    tag_id  BIGINT NOT NULL,

    CONSTRAINT pk_post_tags PRIMARY KEY (post_id, tag_id),
    CONSTRAINT fk_pt_post   FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    CONSTRAINT fk_pt_tag    FOREIGN KEY (tag_id)   REFERENCES tags(id)  ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. comments  (depends on posts)
CREATE TABLE IF NOT EXISTS comments (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    post_id     BIGINT       NOT NULL,
    author_name VARCHAR(100) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    body        TEXT         NOT NULL,
    approved    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_comments      PRIMARY KEY (id),
    CONSTRAINT fk_comments_post FOREIGN KEY (post_id)
        REFERENCES posts (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_comments_post_id  ON comments (post_id);
CREATE INDEX idx_comments_approved ON comments (approved);
