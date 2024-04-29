<template>
    <div id="codeArea">
        <h1>사용자 코드를 입력해주세요.</h1>
        <p><span class="point">※ 사용자 코드란?</span><span>신청하신 사용자에게 부여되는 고유 코드입니다.</span></p>
        <login-form @submit="onsubmit" />
    </div>
</template>

<script>
import LoginForm from '@/components/user/LoginForm.vue'
import api from '@/api'

export default {
  name: 'Login',
  components: {
    LoginForm
  },
  methods: {
    onsubmit (payload) {
      const id = payload.userId
      const pw = payload.userPw
      api.post('/api/login', {id, pw})
        .then(res => {
          if (res.data === 'fail') {
            alert('아이디와 비밀번호를 다시 확인해주세요')
          } else {
            console.log(res.data.token)
            // this.$session.set('userId', id) // 임시
            // this.$session.set('userNm', res.data) // 임시
            // this.$router.push({name: 'Enter'})
          }
        }).catch(error => {
          console.log(error)
        })
    }
  }
}
</script>
