<template>
    <div>
        <h2 id="guest">This IPs visited https://cv.colarietitosti.info/</h2>
        <v-card id="guest" v-bind:key="visitor.id" v-for="visitor in visitors"
            flat
        >
            <h5>{{visitor.ip_addr}}</h5> visited at <h5>{{visitor.timestamp}}</h5>
        </v-card>
    </div>
</template>

<script>
    import axios from 'axios';

    export default {
        name: "visitors",
        props: {
            visitors : []
        },
        mounted(){
            axios.get('/visitors')
                    .then(response => { this.visitors = response.data; })
        }
    }
</script>

<style scoped>
    #guest{
        margin-bottom: 5px;
        width: 80%;
        margin-left: 10%;
    }
</style>