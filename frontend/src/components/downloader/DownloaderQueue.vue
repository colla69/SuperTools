<template>
    <div>
        <v-toolbar flat dense style="margin: 10px">
            <v-btn id="btn" @click="startDownload" :disabled="searchRunning" >Start Download Series</v-btn>
            <v-btn id="btn"  @click="uploadSeries" :disabled="disableSyncButton">Upload Series</v-btn>
            <v-spacer></v-spacer>
            <v-card v-if="syncRunning" flat id="status-alert">Syncing Plex</v-card>
            <v-card v-if="queueRunning" flat id="status-alert" >Queue Running</v-card>
            <v-card v-if="searchRunning" flat id="status-alert" >Search Running</v-card>
            <v-spacer></v-spacer>
            <v-btn id="btn"  @click="clear" >clear queue</v-btn>
        </v-toolbar>
        <div class="queues"  >
            <Downloader style="margin-left: 10px;margin-right: 5px;" title="Todo" v-bind:queue="todo"></Downloader>
            <Downloader title="Downloading" v-bind:queue="downloading"></Downloader>
            <Downloader style="margin-left: 5px;" title="Done" v-bind:queue="done"></Downloader>
        </div>
    </div>
</template>

<script>
    import axios from 'axios';
    import Downloader from "./Downloader";

    export default {
        name: "DownloaderQueue",
        data() {
            return {
                syncRunning: false,
                queueRunning: false,
                searchRunning: false,
                disableSyncButton: false,
                todo: [],
                downloading: [],
                done: [],
            };
        },
        components: {
            Downloader
        },
        methods: {
            startDownload: function () {
                this.searchRunning = true;
                axios.post('/backend/startDownloads');
            },
            clear: function () {
                axios.get('/backend/clearQueue');
            },
            uploadSeries: function () {
                this.syncRunning = true;
                axios.get('/backend/syncTvShows');
            },
            refresh: function () {
                axios.get('/backend/queues')
                    .then(response => {
                        let queues = response.data;
                        this.todo = queues["todo"];
                        this.downloading = queues["downloading"];
                        this.done = queues["done"];
                        this.syncRunning = queues["syncRunning"];
                        this.queueRunning = queues["queueRunning"];
                        this.searchRunning = queues["searchRunning"];
                        this.disableSyncButton = this.syncRunning || this.queueRunning || this.searchRunning;
                    });
                this.componentKey += 1;
            }
        },
        mounted(){
            this.refresh();
            setInterval(this.refresh, 5000);
        },
        beforeDestroy () {
            clearInterval(this.timer)
        }
    }
</script>

<style scoped>
    .queues{
        display: grid;
        grid-template-columns: 33% 33% 33% ;
    }
    #status-alert{
        margin-right: 10px;
    }
    #btn{
        margin-right: 5px;
    }

</style>