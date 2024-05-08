// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import store from './store'
import Cookies from 'js-cookie'

Vue.config.productionTip = false

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
    store,
    components: { App },
    template: '<App/>'
  })
})
