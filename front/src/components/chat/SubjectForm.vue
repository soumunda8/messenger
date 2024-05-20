<template>
  <div id="subjectArea" @click="closeSubject">
    <div :style="subjectImgBg">
      <button>X</button>
    </div>
  </div>
</template>

<script>
import subjectImg from '@/assets/image/subject01.png'

export default {
  name: 'SubjectForm',
  data () {
    return {
      subjectImgBg: {
        backgroundImage: `url(${subjectImg})`
      }
    }
  },
  methods: {
    closeSubject () {
      this.$emit('toggle-subject')
    }
  }
}
</script>

<style scoped>
#subjectArea {
  background-color: #d9d9d9;
  z-index: 10;
  position: absolute;
  left: 10px;
  right: 10px;
  top: 10px;
  bottom: 10px;
}
#subjectArea > div {
  background-size: auto;
    background-position: center;
    background-repeat: no-repeat;
    position: relative;
    width: 100%;
    height: 90vh;
    top: 3vh;
}
</style>
