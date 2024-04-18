import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/pages/user/Login'
import Chat from '@/pages/chat/Chat'
import Enter from '@/pages/chat/Enter'
import Create from '@/pages/chat/Create'

Vue.use(Router)

const router = new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'Login',
      component: Login
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
  if (!userId && to.name !== 'Login') {
    next({ name: 'Login' })
  } else {
    next()
  }
})

export default router
