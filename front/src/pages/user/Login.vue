<template>
    <div id="codeArea">
        <h1>사용자 코드를 입력해주세요.</h1>
        <p><span class="point">※ 사용자 코드란?</span><span>신청하신 사용자에게 부여되는 고유 코드입니다.</span></p>
        <p class="errMsg" v-if="errorMessage">{{ errorMessage }}</p>
        <login-form @submit="onsubmit" />
    </div>
</template>

<script>
import LoginForm from '@/components/user/LoginForm.vue'
import { mapActions } from 'vuex'

export default {
  name: 'Login',
  components: {
    LoginForm
  },
  data () {
    return {
      errorMessage: ''
    }
  },
  methods: {
    onsubmit (payload) {
      this.signin(payload)
        .then(res => {
          alert('로그인이 완료되었습니다.')
          this.$router.push({name: 'Enter'})
        })
        .catch(err => {
          console.log(err)
          if (err.response && err.response.status === 401) {
            this.errorMessage = '아이디나 비밀번호를 다시 확인해주세요.'
          } else {
            this.errorMessage = '잠시 후 다시 시도해주세요.'
          }
        })
    },
    ...mapActions([ 'signin' ])
  }
}
</script>

<style scoped>
#codeArea p.errMsg {font-weight: bold;color: red;padding: 0 0 5vh;}
</style>
