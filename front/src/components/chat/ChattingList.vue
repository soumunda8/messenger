<template>
  <div id="chatArea" ref="chattingDiv">
    <ul>
      <li v-for="(chat, index) in chatList" :key="index">
        <div v-if="chat.messagetype === 'enter' || chat.messagetype === 'out' || chat.messagetype === 'create'" class="center">
          {{chat.message}}
        </div>
        <div v-else-if="chat.senderid === currentUserId">
          <div :class="{'right': true, ' file': chat.messagetype === 'upload'}">
            <!--<div class="chat" v-html="chat.message"></div>-->
            <div class="chat" v-html="processMessage(chat)"></div>
            <div class="thumbnail"></div>
          </div>
        </div>
        <div v-else-if="chat.senderid != currentUserId">
          <div :class="{'left': true, ' file': chat.messagetype === 'upload'}">
            <div class='sender'>
              <div class='thumbnail'></div>
              <p class='senderName'>{{chat.sendernm}}</p>
            </div>
            <!--<div class="chat" v-html="chat.message"></div>-->
            <div class="chat" v-html="processMessage(chat)"></div>
          </div>
        </div>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'ChattingList',
  props: {
    chatList: {
      type: Array
    }
  },
  computed: {
    currentUserId () {
      return this.$session.get('userId')
    }
  },
  methods: {
    updateScroll () {
      this.$nextTick(() => {
        const chattingDiv = this.$refs.chattingDiv
        if (chattingDiv) {
          chattingDiv.scrollTop = chattingDiv.scrollHeight
        }
      })
    },
    processMessage (chat) {
      if (chat.messagetype === 'upload') {
        let downloadUrl = `/util/download?id=${chat.id}`
        let imageUrl = `/util/upload/${chat.userid}/${chat.roomid}/${chat.filenm}.png`
        return chat.message
          .replace(/href="[^"]*"/, `href="${downloadUrl}"`)
          .replace(/src="[^"]*"/, `src="${imageUrl}"`)
      }
      return chat.message
    }
  },
  mounted () {
    this.updateScroll()
  },
  updated () {
    this.updateScroll()
  }
}
</script>
