<template>
  <div id="chatArea" ref="chattingDiv">
    <ul ref="chattingtdd">
      <li v-for="(chat, index) in chatList" :key="index">
        <div v-if="chat.messagetype === 'enter' || chat.messagetype === 'out' || chat.messagetype === 'create'" class="center">
          {{chat.message}}
        </div>
        <div v-else-if="chat.senderid === currentUserId">
          <div :class="{'right': true, 'file': chat.messagetype === 'upload' || chat.messagetype === 'upload_img'}">
            <div class="chat">
              <div v-if="chat.messagetype === 'canvas'">
                <img class="chat-image" :src="getImageSrc(chat)" @click="openCanvas(chat)" @load="updateScroll" />
              </div>
              <div v-else v-html="processMessage(chat)"></div>
            </div>
            <div class="thumbnail"></div>
          </div>
        </div>
        <div v-else-if="chat.senderid != currentUserId">
          <div :class="{'left': true, 'file': chat.messagetype === 'upload' || chat.messagetype === 'upload_img'}">
            <div class='sender'>
              <div class='thumbnail'></div>
              <p class='senderName'>{{chat.sendernm}}</p>
            </div>
            <div class="chat">
              <div v-if="chat.messagetype === 'upload' || chat.messagetype === 'upload_img'" v-html="processMessage(chat)"></div>
              <div v-else-if="chat.messagetype === 'canvas'">
                <img class="chat-image" :src="getImageSrc(chat)" @click="openCanvas(chat)" @load="updateScroll" />
              </div>
              <div v-else>
                {{chat.message}}
              </div>
            </div>
          </div>
        </div>
      </li>
    </ul>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'ChattingList',
  props: {
    chatList: {
      type: Array
    }
  },
  computed: {
    ...mapState(['me']),
    currentUserId () {
      return this.me ? this.me.userId : null
    }
  },
  updated () {
    this.updateImageLoad()
    this.updateScroll()
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
      let innerMsg = ''
      if (chat.messagetype === 'upload' || chat.messagetype === 'upload_img') {
        innerMsg = `<a href='/util/download?id=${chat.fid}' download>`
        if (chat.messagetype === 'upload') {
          innerMsg += chat.message
        } else {
          innerMsg += `<img class='chat-image' src='/util/upload/${chat.senderid}/${chat.roomid}/${chat.message}' />`
        }
        innerMsg += '</a>'
      } else {
        innerMsg = chat.message
      }
      return innerMsg
    },
    getImageSrc (chat) {
      return `/util/upload/drawing/${chat.roomid}/${chat.message}`
    },
    openCanvas (chat) {
      this.$emit('toggle-canvas', this.getImageSrc(chat))
    },
    updateImageLoad () {
      const images = this.$el.querySelectorAll('.chat-image')
      images.forEach(image => {
        image.onload = () => {
          this.updateScroll()
        }
      })
    }
  }
}
</script>
