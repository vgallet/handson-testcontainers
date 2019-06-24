module.exports = {
    title: "Dockerisez vos tests d’intégration",
    description: "Mise en place de testcontainers pour l'application Spring Petclinic",
    base: "/handson-testcontainers/",
    port: 3000,
    dest: 'public/docs',
    themeConfig: {
        // Assumes GitHub. Can also be a full GitLab url.
        repo: 'vgallet/handson-testcontainers',
        // defaults to false, set to true to enable
        editLinks: true,
        // custom text for edit link. Defaults to "Edit this page"
        editLinkText: 'Help us improve this page!',
        // if your docs are not at the root of the repo:
        docsDir: 'tps',
        sidebar: [
            '/1_GETTING_STARTED',
            '/2_ARCHITECTURE',
            '/3_MYSQL_CONTAINER',
            '/4_WEB_BROWSER_CONTAINER',
            '/5_PAGE_PATTERN'
        ]
    }
};
