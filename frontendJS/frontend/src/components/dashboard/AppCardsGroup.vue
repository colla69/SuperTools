<template>
    <div class="line" >
        <div :id="title" v-bind:key="card.id" v-for="card in items" >
            <AppCard :name="card.name" :link="card.link" :imglink="card.imglink">
            </AppCard>
        </div>
    </div>
</template>

<script>
    import axios from 'axios';
    import AppCard from "./AppCard";

    export default {
        name: "AppCardsGroup",
        components: {
            AppCard
        },
        props: {
            "title": String,
            "items":[],
        },
        mounted(){
            axios.get('/dashApps')
                .then(response => this.items = response.data)
        }
    }
</script>

<style scoped>
    .line{
        display: inline-grid;
        grid-template-columns: auto auto auto auto auto auto auto ;
        grid-template-rows: auto auto auto  ;
    }
</style>