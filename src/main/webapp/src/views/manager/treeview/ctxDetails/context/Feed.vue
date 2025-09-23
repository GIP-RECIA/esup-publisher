<template>
  <div v-if="context.type === 'EXTERNALFEED'">
    <ExternalFeed></ExternalFeed>
  </div>
  <div v-else-if="context.type === 'INTERNALFEED'">
    <InternalFeed></InternalFeed>
  </div>
</template>

<script>
import ExternalFeed from './ExternalFeed.vue';
import InternalFeed from './InternalFeed.vue';
export default {
  name: 'Feed',
  components: {
    ExternalFeed,
    InternalFeed,
  },
  inject: ['context'],
};
</script>
