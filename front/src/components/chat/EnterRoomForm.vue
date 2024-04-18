<template>
  <div id="right">
    <div class="content inter">
      <div id="codeArea">
        <h1>채팅방 코드를 입력해주세요.</h1>
        <p><span class="point">※ 채팅방 코드란?</span><span>생성한 채팅방에 자동으로 부여되는 고유 코드입니다.</span></p>
        <input type="number" v-model="roomId" placeholder="채팅방 코드 입력" />
        <button type="button" @click.prevent="enterRoom">확인</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'EnterRoomForm',
  data () {
    return {
      roomId: ''
    }
  },
  methods: {
    enterRoom () {
      const roomid = this.roomId
      if (!roomid) {
        alert('채팅방 코드를 입력해주세요')
        return
      }
      this.$emit('enterRoom', {roomid})
    }
  }
}
</script>
