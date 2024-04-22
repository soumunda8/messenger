<template>
  <div id="chatForm">
    <file-send-form ref="fileSendForm" @toggle-canvas="relayEvent" @file-prepared="handleFilePrepared" :uploadStatus="uploadStatus"/>
    <div class="chatting_form">
      <label for="chatInput">
        <textarea id="chatInput" v-model="message"></textarea>
      </label>
      <div class="btn_input_area">
        <button type="button" class="moreBtn" @click="fileUploadBtn()">+</button>
        <button type="button" class="submitBtn" @click.prevent="submitChat()">전송</button>
      </div>
    </div>
  </div>
</template>

<script>
import FileSendForm from '@/components/chat/FileSendForm.vue'

export default {
  name: 'ChattingForm',
  components: {
    FileSendForm
  },
  props: {
    uploadStatus: String
  },
  data () {
    return {
      message: ''
    }
  },
  methods: {
    fileUploadBtn () {
      this.$refs.fileSendForm.openFileUpload()
    },
    submitChat () {
      const message = this.message
      if (!message) {
        alert('메세지를 입력하세요')
        return
      }
      this.$emit('submitChat', {message})
    },
    clearMessage () {
      this.message = ''
    },
    handleFilePrepared (fileData) {
      this.$emit('file-prepared', fileData)
    },
    relayEvent () {
      this.$emit('toggle-canvas')
    }
  }
}
</script>
