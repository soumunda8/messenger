<template>
  <div id="filePopupArea" v-show="showFilePopupArea">
    <div id="inputArea" v-show="showInputArea">
      <input type="file" id="uploadImageFile" v-show="showImageFile" name="uploadFile1" accept="image/*" @change="prepareFileUpload($event, 1)" multiple>
      <input type="file" id="uploadAllFile" v-show="showAllFile" name="uploadFile2" accept="*/*"  @change="prepareFileUpload($event, 2)" multiple>
    </div>
    <ul>
      <li><button type="button" @click="openFile(1)">앨범</button></li>
      <li><button type="button" @click="openFile(2)">파일</button></li>
      <li><button type="button" @click="openDraw()">드로잉</button></li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'FileSendForm',
  props: {
    uploadStatus: String
  },
  data () {
    return {
      showInputArea: false,
      showImageFile: false,
      showAllFile: false,
      showFilePopupArea: false
    }
  },
  watch: {
    uploadStatus (newStatus) {
      if (newStatus === 'success') {
        this.showInputArea = false
        this.showImageFile = false
        this.showAllFile = false
      }
    }
  },
  methods: {
    openFile (type) {
      if (type === 1) {
        this.showInputArea = true
        this.showImageFile = true
        this.showAllFile = false
      } else if (type === 2) {
        this.showInputArea = true
        this.showImageFile = false
        this.showAllFile = true
      }
    },
    openDraw () {
      this.showInputArea = false
      this.showImageFile = false
      this.showAllFile = false
    },
    openFileUpload () {
      this.showFilePopupArea = !this.showFilePopupArea
    },
    prepareFileUpload (event, fileType) {
      const files = event.target.files
      if (files.length === 0) return
      this.$emit('file-prepared', { files })
    }
  }
}
</script>
