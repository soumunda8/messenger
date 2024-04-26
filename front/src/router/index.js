import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/pages/user/Login'
import Join from '@/pages/user/Join'
import Chat from '@/pages/chat/Chat'
import Enter from '@/pages/chat/Enter'
import Create from '@/pages/chat/Create'
import Main from '@/pages/Main'

Vue.use(Router)

const router = new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'Main',
      component: Main
    },
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/join',
      name: 'Join',
      component: Join
    },
    {
      path: '/enter',
      name: 'Enter',
      component: Enter
    },
    {
      path: '/create',
      name: 'Create',
      component: Create
    },
    {
      path: '/chat/:roomId',
      name: 'Chat',
      component: Chat
    }
  ]
})

router.beforeEach((to, from, next) => {
  const userId = Vue.prototype.$session.get('userId')
  const publicPages = ['Login', 'Join', 'Main']
  const authRequired = !publicPages.includes(to.name)
  if (!userId && authRequired) {
    next({ name: 'Main' })
  } else if (userId && publicPages.includes(to.name)) {
    next({ name: 'Enter' })
  } else {
    next()
  }
})

export default router
