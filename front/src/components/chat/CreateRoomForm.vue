<template>
  <div id="right">
    <div class="content inter">
      <div id="codeArea">
        <h1 style="margin-bottom:5rem">채팅방 이름을 입력해주세요.</h1>
        <input type="text" v-model="roomnm" placeholder="채팅방 이름 입력" />
        <button type="button" @click.prevent="createRoom">확인</button>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  name: 'CreateRoomForm',
  data () {
    return {
      roomnm: ''
    }
  },
  methods: {
    createRoom () {
      const roomnm = this.roomnm
      if (!roomnm) {
        alert('채팅방 이름을 입력해주세요')
        return
      }
      this.$emit('createRoom', {roomnm})
    }
  }
}
</script>
