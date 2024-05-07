// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import store from './store'
// import VueSession from 'vue-session'
import Cookies from 'js-cookie'

Vue.config.productionTip = false
/*
var sessionOptions = {
  persist: true
}

Vue.use(VueSession, sessionOptions)
*/
function init () {
  const saveToken = Cookies.get('accessToken')
  if (saveToken) {
    return store.dispatch('signinByToken', saveToken)
  }
  return Promise.resolve()
}

/* eslint-disable no-new */
init().then(() => {
  new Vue({
    el: '#app',
    router,
    components: { App },
    template: '<App/>',
    store
  })
})
