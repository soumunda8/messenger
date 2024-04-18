<template>
  <div id="filePopupArea" v-show="showFilePopupArea">
    <div id="inputArea" v-show="showInputArea">
      <input type="file" id="uploadImageFile" v-show="showImageFile" name="uploadFile1" accept="image/*" @change="fileUpload(1)" multiple>
      <input type="file" id="uploadAllFile" v-show="showAllFile" name="uploadFile2" accept="*/*"  @change="fileUpload(2)" multiple>
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
  data () {
    return {
      showInputArea: false,
      showImageFile: false,
      showAllFile: false,
      showFilePopupArea: false
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
    }
  }
}
</script>
