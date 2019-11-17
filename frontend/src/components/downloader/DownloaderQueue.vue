<template>
    <div>
        <v-toolbar flat dense style="margin: 10px">
                <v-btn id="btn" @click="startDownload"  >Start Download Series</v-btn>
                <v-btn id="btn"  @click="uploadSeries" >Upload Series</v-btn>
                <v-spacer></v-spacer>
                <v-btn id="btn"  @click="clear" >clear queue</v-btn>
        </v-toolbar>
        <div class="queues" :key="componentKey" >
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
        props: {
            todo: [],
            downloading: [],
            done: [],
            running: Boolean
        },
        data() {
            return {
                componentKey: 0,
            };
        },
        components: {
            Downloader
        },
        methods: {
            startDownload: function () {
                axios.post('/backend/startDownloads');
            },
            clear: function () {
                axios.get('/backend/clearQueue');
            },
            uploadSeries: function () {
                axios.get('/backend/syncTvShows');
            },
            refresh: function () {
                axios.get('/backend/queues')
                    .then(response => {
                        let queues = response.data;
                        this.todo = queues["todo"];
                        this.downloading = queues["downloading"];
                        this.done = queues["done"];
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
    #btn{
        margin-right: 5px;
    }
</style>