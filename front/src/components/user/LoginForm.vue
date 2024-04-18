<template>
    <form @submit.prevent="submit">
        <input type="text" v-model="userId" placeholder="사용자 코드 입력" style="margin-bottom:0.5rem" />
        <input type="password" v-model="userPw" placeholder="사용자 비밀번호 입력" />
        <button type="submit">로그인</button>
    </form>
</template>

<script>
export default {
  name: 'LoginForm',
  data () {
    return {
      userId: '',
      userPw: ''
    }
  },
  methods: {
    submit () {
      const {userId, userPw} = this
      if (!userId) {
        alert('아이디를 입력해주세요')
        return
      }
      if (!userPw) {
        alert('비밀번호를 입력해주세요')
        return
      }
      this.$emit('submit', {userId, userPw})
    }
  }
}
</script>
