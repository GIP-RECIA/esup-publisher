<script>
import ConfigurationService from '@/services/admin/ConfigurationService.js'

export default {
  name: 'AdminConfiguration',
  data() {
    return {
      // Configuration
      configuration: [],
      // Filtre appliqué sur les loggers
      filter: null,
      // Sens du tri des loggers
      reverse: false,
      // Propriété des loggers sur laquelle le tri est effectué
      predicate: null,
    }
  },
  computed: {
    filteredConfiguration() {
      let filterConfiguration = this.configuration

      // Filtre des loggers
      if (this.filter !== null && this.filter !== '') {
        filterConfiguration = filterConfiguration.filter(
          conf => conf.prefix.includes(this.filter) || JSON.stringify(conf.properties).includes(this.filter),
        )
      }

      // Tri des loggers
      if (this.predicate !== null) {
        filterConfiguration.sort((conf1, conf2) => conf1[this.predicate].localeCompare(conf2[this.predicate]) * (this.reverse ? -1 : 1))
      }

      return filterConfiguration
    },
  },
  created() {
    ConfigurationService.get().then((configuration) => {
      const properties = []
      const propertiesObject = this.getConfigPropertiesObjects(configuration)
      Object.keys(propertiesObject).forEach((key) => {
        if (Object.hasOwn(propertiesObject, key)) {
          properties.push(propertiesObject[key])
        }
      })
      this.configuration = this.configuration.concat(properties)
    })
    ConfigurationService.getEnv().then((response) => {
      const properties = []
      const propertiesObject = response.data.propertySources
      propertiesObject.forEach((obj) => {
        const name = obj.name
        const detailProperties = obj.properties
        const vals = {}
        Object.keys(detailProperties).forEach((key) => {
          vals[key] = detailProperties[key].value
        })
        properties.push({ prefix: name, properties: vals })
      })
      this.configuration = this.configuration.concat(properties)
    })
  },
  methods: {
    setSorting(predicate) {
      this.predicate = predicate
      this.reverse = !this.reverse
    },
    getConfigPropertiesObjects(res) {
      return res[0]['ESUP-Publisher'].beans
    },
  },
}
</script>

<template>
  <div>
    <h2>{{ $t('configuration.title') }}</h2>

    {{ $t('configuration.filter') }}
    <input v-model="filter" type="text" class="form-control">

    <table class="table table-sm table-striped table-bordered table-responsive" style="table-layout: fixed">
      <thead>
        <tr>
          <th @click="setSorting('prefix')">
            {{ $t('configuration.table.prefix') }}
          </th>
          <th>{{ $t('configuration.table.properties') }}</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="entry in filteredConfiguration" :key="entry.prefix">
          <td>
            <span>{{ entry.prefix }}</span>
          </td>
          <td>
            <div v-for="(value, key) in entry.properties" :key="key" class="row">
              <div class="col-md-6">
                {{ key }}
              </div>
              <div class="col-md-6">
                <span class="float-end badge bg-info" style="white-space: normal; word-break: break-all">{{ value }}</span>
              </div>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
