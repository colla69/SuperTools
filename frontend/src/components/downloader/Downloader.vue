<template>
    <v-card id="list">
        <h3>{{title}}</h3>
        <div id="list" dense>
            <p style="min-height: 25px;" v-for="q in queue" v-bind:key="q">
                {{q}}
            </p>
        </div>
    </v-card>
</template>

<script>
    export default {
        name: "downloader",
        props: {
            title: String,
            queue: Array
        }
    }
</script>

<style scoped>
    h3{
        width: 100%;
        margin: 7px;
    }
    #list{
        height: 100%;
    }
    p{
        width: 100%;
        margin-bottom: 4px;
        text-align: left;
        padding-left: 10px;
    }
</style>