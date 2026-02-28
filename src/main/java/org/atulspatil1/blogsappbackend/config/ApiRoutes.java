package org.atulspatil1.blogsappbackend.config;

public final class ApiRoutes {

    private ApiRoutes() {
    }

    public static final String LEGACY_BASE = "/api";
    public static final String V1_BASE = "/api/v1";

    public static final String AUTH_V1 = V1_BASE + "/auth";
    public static final String AUTH_LEGACY = LEGACY_BASE + "/auth";

    public static final String POSTS_V1 = V1_BASE + "/posts";
    public static final String POSTS_LEGACY = LEGACY_BASE + "/posts";

    public static final String CATEGORIES_V1 = V1_BASE + "/categories";
    public static final String CATEGORIES_LEGACY = LEGACY_BASE + "/categories";

    public static final String TAGS_V1 = V1_BASE + "/tags";
    public static final String TAGS_LEGACY = LEGACY_BASE + "/tags";

    public static final String COMMENTS_V1 = V1_BASE + "/comments";
    public static final String COMMENTS_LEGACY = LEGACY_BASE + "/comments";

    public static final String[] COMMENTS = {COMMENTS_V1, COMMENTS_LEGACY};

    public static final String LOGIN_V1 = AUTH_V1 + "/login";
    public static final String LOGIN_LEGACY = AUTH_LEGACY + "/login";

    public static final String POSTS_ALL_V1 = POSTS_V1 + "/**";
    public static final String POSTS_ALL_LEGACY = POSTS_LEGACY + "/**";

    public static final String CATEGORIES_ALL_V1 = CATEGORIES_V1 + "/**";
    public static final String CATEGORIES_ALL_LEGACY = CATEGORIES_LEGACY + "/**";

    public static final String TAGS_ALL_V1 = TAGS_V1 + "/**";
    public static final String TAGS_ALL_LEGACY = TAGS_LEGACY + "/**";

    public static final String COMMENTS_ALL_V1 = COMMENTS_V1 + "/**";
    public static final String COMMENTS_ALL_LEGACY = COMMENTS_LEGACY + "/**";
}
