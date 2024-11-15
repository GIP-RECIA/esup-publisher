<script>
import Empty from './Empty.vue'
import Flash from './Flash.vue'
import Media from './Media.vue'
import News from './News.vue'
import Resource from './Resource.vue'

export default {
  name: 'TabItem',
  components: {
    News,
    Media,
    Resource,
    Flash,
    Empty,
  },
  inject: ['context'],
}
</script>

<template>
  <div v-if="context.type === 'NEWS'">
    <News />
  </div>
  <div v-else-if="context.type === 'MEDIA'">
    <Media />
  </div>
  <div v-else-if="context.type === 'RESOURCE'">
    <Resource />
  </div>
  <div v-else-if="context.type === 'FLASH'">
    <Flash />
  </div>
  <div v-else>
    <Empty />
  </div>
</template>
