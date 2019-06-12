// .vuepress/plugins.js
//import Vue from 'vue'

import vueCollapse from 'vue2-collapse'

export default ({
    Vue, // the version of Vue being used in the VuePress app
    options, // the options for the root Vue instance
    router, // the router instance for the app
    siteData // site metadata
}) => {
//    if (typeof process === 'undefined') { // process is undefined in a browser
        // Loading the plugin into the Vue.
        Vue.use(vueCollapse)
//    }
}

