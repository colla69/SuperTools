<template>
    <v-card raised class="wrap">
        <h2> {{this.title}} </h2>
        <h4>Crawlers</h4>
        <div class="line">
            <AppCard :name="card.name" :link="card.link" :imglink="card.imglink" v-bind:key="card.id" v-for="card in utils">
            </AppCard>
        </div>
        <br>
        <h4>Links</h4>
        <div class="line">
            <AppCard :name="card.name" :link="card.link" :imglink="card.imglink" v-bind:key="card.id" v-for="card in links">
            </AppCard>
        </div>
        <h4>TV</h4>
        <div class="line">
            <AppCard :name="card.name" :link="card.link" :imglink="card.imglink" v-bind:key="card.id" v-for="card in tv">
            </AppCard>
        </div>
    </v-card>
</template>

<script>
    import axios from 'axios';
    import AppCard from "./AppCard";

    export default {
        name: "AppCardsGroup",
        components: {
            AppCard
        },
        data() {
            return{
                title: "",
                tv: [],
                utils: [],
                links: [],
            };
        },
        methods: {
            load: function () {
                axios.get('/backend/dashApps')
                    .then(response => {
                        let apps = response.data;
                        this.tv = apps["dashTv"];
                        this.links = apps["dashApps"];
                        this.utils = apps["dashUtils"];
                    })
            },
        },
        created(){
            this.load();
        }
    }
</script>

<style scoped>
    .wrap {
        width: fit-content;
        background: lightcyan;

        margin: 10px;
    }
    .line{
        display: grid;
        width: fit-content;

        grid-template-columns: auto auto auto auto auto auto auto;
        grid-template-rows: auto auto auto;
    }
    h4{
        margin-bottom: 3px;
        border-top: 1px solid skyblue;
    }
</style>