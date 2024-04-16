import { USERID, USERNM } from './mutations-types'
import api from '@/api'

export default {
  loginPro ({ commit }, { id, pw }) {
    return api.post('/login', {id, pw})
      .then(res => {
        if (res.data === 'fail') {
          alert('아이디와 비밀번호를 다시 확인해주세요')
        } else {
          // 세션 설정을 컴포넌트로 이동
          commit(USERID, id)
          commit(USERNM, res.data)
          return res.data // 데이터를 반환
        }
      })
  }
}
