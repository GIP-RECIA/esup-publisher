import antfu from '@antfu/eslint-config'

export default antfu({
  formatters: true,
  vue: {
    overrides: {
      'vue/attribute-hyphenation': ['off'],
    },
  },
})
