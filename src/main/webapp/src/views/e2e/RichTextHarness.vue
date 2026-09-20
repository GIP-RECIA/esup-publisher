<script setup>
import { nextTick, onMounted, provide, ref } from 'vue'
import RichText from '@/components/richtext/RichText.vue'
import ConfigurationService from '@/services/params/ConfigurationService.js'

const body = ref('<p>Initial content <i class="fas fa-file-pdf fa-lg">&nbsp;</i></p>')
const linkedFiles = ref([])
const richText = ref()

ConfigurationService.confCKEditor = {
  mediaUrlPattern: '/^(?:(?:https?:)?\\/\\/)?(pod\\.univ\\.fr\\/video|.*\\.fr\\/POD\\/video)\\/(.*)\\/(\\?is_iframe=true)?$/',
}
ConfigurationService.confImageSize = 1000000

provide('publisher', { context: { organization: { id: 42 } } })
provide('linkedFilesToContent', linkedFiles)
provide('setLinkedFilesToContent', (value) => {
  linkedFiles.value = value
})

function uploaded(file, url) {
  linkedFiles.value = [...linkedFiles.value, { uri: url, filename: file.name, inBody: true, contentType: file.type }]
}

onMounted(async () => {
  await nextTick()
  const exposeEditor = () => {
    if (richText.value?.editorState) {
      window.__richTextEditor = richText.value.editorState
      return
    }
    window.setTimeout(exposeEditor, 20)
  }
  exposeEditor()
})
</script>

<template>
  <main>
    <RichText
      ref="richText"
      v-model="body"
      :entity-id="42"
      :image-size-max="1000000"
      :file-size-max="1000000"
      error-image-size-msg="Image too large"
      error-file-size-msg="File too large"
      :call-back-success="uploaded"
    />
    <output data-testid="editor-data">{{ body }}</output>
    <output data-testid="linked-files">{{ JSON.stringify(linkedFiles) }}</output>
  </main>
</template>
