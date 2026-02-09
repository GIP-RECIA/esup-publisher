<script>
import EnumDatasService from '@/services/entities/enum/EnumDatasService.js'
import RedactorService from '@/services/entities/redactor/RedactorService.js'

export default {
  name: 'RedactorDetail',
  data() {
    return {
      redactor: {
        name: null,
        displayName: null,
        description: null,
        format: null,
        writingMode: null,
        nbLevelsOfClassification: 1,
        optionalPublishTime: false,
        nbDaysMaxDuration: 168,
        id: null,
      },
    }
  },
  computed: {
    // Méthode de récupération de la liste de writing
    writingModeList() {
      return EnumDatasService.getWritingModeList()
    },
  },
  created() {
    this.initData()
  },
  methods: {
    // Méthode de récupération de l'objet grâce à l'id passé en paramètre
    initData() {
      RedactorService.get(this.$route.params.id)
        .then((response) => {
          this.redactor = response.data
        })
        .catch((error) => {
          console.error(error)
        })
    },
    // Méthode de redirection sur la page listant les redacteurs
    redactorPage() {
      this.$router.push({ name: 'AdminEntityRedactor' })
    },
    getWritingModeLabel(name) {
      if (name) {
        return this.writingModeList.filter((val) => {
          return val.name === name
        })[0].label
      }
      else {
        return ''
      }
    },
  },
}
</script>

<template>
  <div>
    <h2>
      <span>{{ $t('redactor.detail.title') }}</span> {{ $route.params.id }}
    </h2>
    <div class="table-responsive">
      <table class="table table-striped">
        <thead>
          <tr>
            <th>{{ $t('entity.detail.field') }}</th>
            <th>{{ $t('entity.detail.value') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>
              <span>{{ $t('redactor.name') }}</span>
            </td>
            <td>
              <input type="text" class="form-control form-control-sm" :value="redactor.name" readonly>
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('redactor.displayName') }}</span>
            </td>
            <td>
              <input type="text" class="form-control form-control-sm" :value="redactor.displayName" readonly>
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('redactor.description') }}</span>
            </td>
            <td>
              <input type="text" class="form-control form-control-sm" :value="redactor.description" readonly>
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('redactor.format') }}</span>
            </td>
            <td>
              <input type="text" class="form-control form-control-sm" :value="redactor.format" readonly>
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('redactor.writingMode') }}</span>
            </td>
            <td>
              <input type="text" class="form-control form-control-sm" :value="$t(getWritingModeLabel(redactor.writingMode))" readonly>
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('redactor.nbLevelsOfClassification') }}</span>
            </td>
            <td>
              <input type="text" class="form-control form-control-sm" :value="redactor.nbLevelsOfClassification" readonly>
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('redactor.optionalPublishTime') }}</span>
            </td>
            <td>
              <input v-model="redactor.optionalPublishTime" type="checkbox" disabled>
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t('redactor.nbDaysMaxDuration') }}</span>
            </td>
            <td>
              <input type="text" class="form-control form-control-sm" :value="redactor.nbDaysMaxDuration" readonly>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <button type="submit" class="btn btn-info" @click="redactorPage">
      <span class="fas fa-arrow-left" />&nbsp;<span> {{ $t('entity.action.back') }}</span>
    </button>
  </div>
</template>
