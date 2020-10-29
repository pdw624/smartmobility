<template>
    <div id="buttons">
            <el-button v-if="events.refresh != null && events.refresh.show" @click="handleRefresh"          type="info"  size="mini" plain icon="el-icon-refresh">새로고침</el-button>
            <el-button v-if="events.add     != null && events.add.show" @click="handleAdd"                  type="info"  size="mini" plain icon="el-icon-circle-plus-outline">추가</el-button>
            <el-button v-if="events.delete  != null && events.delete.show" @click="handleDelete"            type="info"  size="mini" plain icon="el-icon-remove-outline">삭제</el-button>
            <el-button v-if="events.save    != null && events.save.show" @click="handleSave"                type="info"  size="mini" plain icon="el-icon-folder-checked">저장</el-button>
            <el-button v-if="events.execute != null && events.execute.show" @click="handleExecute"          type="info"  size="mini" plain icon="el-icon-video-play" :disabled="getIsExecute">실행</el-button>
            <el-button v-if="events.pause   != null && events.pause.show" @click="handlePause"              type="info"  size="mini" plain icon="el-icon-video-pause" :disabled="!getIsExecute">중지</el-button>
            <el-button v-if="events.download   != null && events.download.show" @click="handleDownload"     type="info"  size="mini" plain icon="el-icon-download">다운로드</el-button>
            <!--박대원-->
            <!--el-button v-if="events.reserve    != null && events.reserve.show" @click="handleReserve"          type="info"  size="mini" plain icon="el-icon-folder-checked">예약</el-button-->
    </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
    props: ['buttonEvents'],
    data() {
        return {
            events: this.buttonEvents
        }
    },

    computed: {
        ...mapGetters([
            'getIsExecute'
        ])
    },

    methods: {
        isNullEvent: function(object) {
            if(typeof object == 'undefined' || object == null) {
                alert('이벤트를 등록해주세요')
                return false
            } else {
                return true
            }
        },



        //박대원
        // handleReserve: function() {
        //     if(this.isNullEvent(this.events.reserve.event))
        //         this.events.reserve.event()
        // },
        //
        handleRefresh: function() {
            if(this.isNullEvent(this.events.refresh.event))
                this.events.refresh.event()
        },
        handleAdd: function() {
            if(this.isNullEvent(this.events.add.event)) {
                this.events.add.event()
            }
        },
        handleDelete: function() {
            if(this.isNullEvent(this.events.delete.event))
                this.events.delete.event()
        },
        handleSave: function() {
            if(this.isNullEvent(this.events.save.event))
                this.events.save.event()
        },
        handleExecute: function() {
            if(this.isNullEvent(this.events.execute.event))
                this.events.execute.event()
        },
        handlePause: function() {
            if(this.isNullEvent(this.events.pause.event))
                this.events.pause.event()
        },
        handleDownload: function() {
            if(this.isNullEvent(this.events.download.event))
                this.events.download.event()
        }
    }
}
</script>

<style scoped>
</style>

<style>
button.el-button+.el-button {
    margin-left: 0;
}

#buttons {
    margin-top: 9px;
    margin-right: 9px;
}
</style>