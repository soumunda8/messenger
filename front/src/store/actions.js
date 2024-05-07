import { SET_ACCESS_TOKEN, SET_MY_INFO, DESTROY_ACCESS_TOKEN, DESTROY_MY_INFO } from './mutations-types'
// import { SET_ACCESS_TOKEN } from './mutations-types'
// import Vue from 'vue'
import api from '@/api'

export default {
  signin ({ commit }, payload) {
    const id = payload.userId
    const pw = payload.userPw
    return api.post('/api/login', {id, pw})
      .then(res => {
        // Vue.prototype.$session.set('userId', res.data.userId)
        // Vue.prototype.$session.set('userNm', res.data.userNm)
        commit(SET_ACCESS_TOKEN, res.data)
        return api.get('/api/users/me')
      }).then(res => {
        commit(SET_MY_INFO, res.data)
      })
  },
  signinByToken ({ commit }, token) {
    commit(SET_ACCESS_TOKEN, token)
    return api.get('/api/users/me')
      .then(res => {
        commit(SET_MY_INFO, res.data)
      })
  },
  signout ({ commit }) {
    commit(DESTROY_MY_INFO)
    commit(DESTROY_ACCESS_TOKEN)
  }
}
