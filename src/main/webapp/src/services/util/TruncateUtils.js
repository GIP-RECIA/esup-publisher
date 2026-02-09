class TruncateUtils {
  // Truncate une chaîne de caractères selon un nombre de caractères
  characters(input, chars, breakOnWord) {
    if (Number.isNaN(chars)) {
      return input
    }
    if (chars <= 0) {
      return ''
    }
    if (input && input.length > chars) {
      input = input.substring(0, chars)

      if (!breakOnWord) {
        const lastspace = input.lastIndexOf(' ')
        // Get last space
        if (lastspace !== -1) {
          input = input.substr(0, lastspace)
        }
      }
      else {
        while (input.charAt(input.length - 1) === ' ') {
          input = input.substr(0, input.length - 1)
        }
      }
      return `${input}...`
    }
    return input
  }

  // Truncate une chaîne de caractères selon un nombre de mots
  words(input, words) {
    if (Number.isNaN(words)) {
      return input
    }
    if (words <= 0) {
      return ''
    }
    if (input) {
      const inputWords = input.split(/\s+/)
      if (inputWords.length > words) {
        input = `${inputWords.slice(0, words).join(' ')}...`
      }
    }
    return input
  }
}
export default new TruncateUtils()
