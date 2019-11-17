<template>
    <v-card id="list">
        <h3>{{title}}</h3>
        <div id="list">
        <p v-for="q in queue" v-bind:key="q">
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
        display: grid;
    }
    p{
        margin-bottom: 4px;
    }
</style>