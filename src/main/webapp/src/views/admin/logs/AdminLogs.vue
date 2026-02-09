<script>
import LogsService from '@/services/admin/LogsService.js'
import TruncateUtils from '@/services/util/TruncateUtils.js'

export default {
  name: 'AdminLogs',
  data() {
    return {
      // Liste des loggers
      loggers: [],
      // Filtre appliqué sur les loggers
      filter: null,
      // Sens du tri des loggers
      reverse: false,
      // Propriété des loggers sur laquelle le tri est effectué
      predicate: null,
    }
  },
  computed: {
    filteredLoggers() {
      let filterLoggers = this.loggers

      // Filtre des loggers
      if (this.filter !== null && this.filter !== '') {
        filterLoggers = filterLoggers.filter(logger => logger.name.includes(this.filter) || logger.level.includes(this.filter))
      }

      // Tri des loggers
      if (this.predicate !== null) {
        filterLoggers.sort((logger1, logger2) => logger1[this.predicate].localeCompare(logger2[this.predicate]) * (this.reverse ? -1 : 1))
      }

      return filterLoggers
    },
  },
  created() {
    LogsService.findAll().then((response) => {
      this.loggers = response.data
    })
  },
  methods: {
    truncateName(name) {
      return TruncateUtils.characters(name, 140)
    },
    setSorting(predicate) {
      this.predicate = predicate
      this.reverse = !this.reverse
    },
    changeLevel(name, level) {
      LogsService.changeLevel(name, level).then(() => {
        LogsService.findAll().then((response) => {
          this.loggers = response.data
        })
      })
    },
  },
}
</script>

<template>
  <div>
    <h2>{{ $t('logs.title') }}</h2>

    <p>{{ $t('logs.nbloggers', { total: loggers.length }) }}</p>

    {{ $t('logs.filter') }}
    <input v-model="filter" type="text" class="form-control">

    <table class="table table-sm table-striped table-bordered table-responsive">
      <thead>
        <tr title="click to order">
          <th @click="setSorting('name')">
            {{ $t('logs.table.name') }}
          </th>
          <th @click="setSorting('level')">
            {{ $t('logs.table.level') }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="logger in filteredLoggers" :key="logger.name">
          <td>
            <small>{{ truncateName(logger.name) }}</small>
          </td>
          <td>
            <button
              :class="{
                'btn-danger': logger.level === 'TRACE',
                'btn-default': logger.level !== 'TRACE',
              }"
              class="btn btn-sm"
              @click="changeLevel(logger.name, 'TRACE')"
            >
              TRACE
            </button>
            <button
              :class="{
                'btn-warning': logger.level === 'DEBUG',
                'btn-default': logger.level !== 'DEBUG',
              }"
              class="btn btn-sm"
              @click="changeLevel(logger.name, 'DEBUG')"
            >
              DEBUG
            </button>
            <button
              :class="{
                'btn-info': logger.level === 'INFO',
                'btn-default': logger.level !== 'INFO',
              }"
              class="btn btn-sm"
              @click="changeLevel(logger.name, 'INFO')"
            >
              INFO
            </button>
            <button
              :class="{
                'btn-success': logger.level === 'WARN',
                'btn-default': logger.level !== 'WARN',
              }"
              class="btn btn-sm"
              @click="changeLevel(logger.name, 'WARN')"
            >
              WARN
            </button>
            <button
              :class="{
                'btn-primary': logger.level === 'ERROR',
                'btn-default': logger.level !== 'ERROR',
              }"
              class="btn btn-sm"
              @click="changeLevel(logger.name, 'ERROR')"
            >
              ERROR
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style lang="scss" scoped>
table {
  tbody {
    .btn {
      padding: 1px 5px;
      font-size: 12px;
      border-radius: 3px;
      margin-left: 2px;
      margin-right: 2px;
      &.btn-default {
        color: #333;
        background-color: #fff;
        border-color: #ccc;
        &:hover,
        &:active,
        &:focus {
          background-color: #e6e6e6;
          border-color: #adadad;
        }
      }
    }
  }
}
</style>
