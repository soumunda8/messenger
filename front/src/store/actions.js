import { SET_ACCESS_TOKEN, SET_MY_INFO, DESTROY_ACCESS_TOKEN, DESTROY_MY_INFO } from './mutations-types'
import api from '@/api'

export default {
  signin ({ commit, dispatch }, payload) {
    const id = payload.userId
    const pw = payload.userPw
    return api.post('/api/login', {id, pw})
      .then(res => {
        commit(SET_ACCESS_TOKEN, res.data)
        return api.get('/api/users/me')
      }).then(res => {
        commit(SET_MY_INFO, res.data)
      }).catch(() => {
        dispatch('signout')
      })
  },
  signinByToken ({ commit, dispatch }, token) {
    commit(SET_ACCESS_TOKEN, token)
    return api.get('/api/users/me')
      .then(res => {
        commit(SET_MY_INFO, res.data)
      }).catch(() => {
        dispatch('signout')
      })
  },
  signout ({ commit }) {
    commit(DESTROY_MY_INFO)
    commit(DESTROY_ACCESS_TOKEN)
  }
}
