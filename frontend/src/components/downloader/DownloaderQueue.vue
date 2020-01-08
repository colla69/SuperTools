<template>
    <div>
        <v-toolbar  dense style="margin: 10px">
            <v-btn id="btn" outlined @click="startDownload" :disabled="searchRunning" >Start Download Series</v-btn>
            <v-spacer></v-spacer>

            <v-card v-if="syncRunning" flat id="status-alert">
                Syncing Plex
            </v-card>
            <v-card v-if="searchRunning" flat id="status-alert" >
                Search Running
             </v-card>
            <v-spacer></v-spacer>
            <v-btn id="btn" outlined @click="clear" >clear queue</v-btn>
        </v-toolbar>

        <v-expansion-panels id="music" >
            <v-expansion-panel>
                <v-expansion-panel-header>Series Tools</v-expansion-panel-header>
                <v-expansion-panel-content >
                    <v-layout >
                        <SeriesConfigurator ></SeriesConfigurator>
                    </v-layout>
                </v-expansion-panel-content>
            </v-expansion-panel>
        </v-expansion-panels>
        <v-expansion-panels id="music" >
            <v-expansion-panel>
                <v-expansion-panel-header>Music Tools</v-expansion-panel-header>
                <v-expansion-panel-content >
                    <v-layout id="musicpanel">
                        <MusicDownloader class="elevation-5" ></MusicDownloader>
                        <MusicTagger class="elevation-5" ></MusicTagger>
                    </v-layout>
                </v-expansion-panel-content>
            </v-expansion-panel>
        </v-expansion-panels>
        <div class="queues">
            <Downloader style="margin-left: 10px;margin-right: 5px;" title="Todo" v-bind:queue="todo"></Downloader>
            <Downloader title="Downloading" v-bind:queue="downloading"></Downloader>
            <Downloader style="margin-left: 5px;" title="Done" v-bind:queue="done"></Downloader>
        </div>
    </div>
</template>

<script>
    import axios from 'axios';
    import Downloader from "./Downloader";
    import MusicDownloader from "./MusicDownloader";
    import MusicTagger from "./MusicTagger";
    import SeriesConfigurator from "./SeriesConfigurator";

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
            Downloader,
            MusicDownloader,
            MusicTagger,
            SeriesConfigurator
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
                        this.disableSyncButton = this.queueRunning || this.syncRunning ;
                    });
                this.componentKey += 1;
            }
        },
        async mounted(){
            this.refresh();
            setInterval(this.refresh, 5000);
        },
    }
</script>

<style scoped>
    #musicpanel{
        display: grid;
        grid-template-columns: auto;
    }
    #music{
        padding-left: 10px;
        padding-right: 10px;
        padding-bottom: 10px;
        width: 100%;

    }
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