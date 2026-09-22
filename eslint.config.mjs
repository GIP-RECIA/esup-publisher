import antfu from '@antfu/eslint-config'

export default antfu({
  formatters: true,
  rules: {
    'e18e/prefer-static-regex': 'off',
  },
  vue: {
    overrides: {
      'vue/attribute-hyphenation': ['off'],
    },
  },
})
