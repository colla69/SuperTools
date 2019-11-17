<template>
    <div>
        <h2 id="guest">This IPs visited https://cv.colarietitosti.info/</h2>
        <v-card id="guest" v-bind:key="visitor.id" v-for="visitor in visitors"
            flat
        >
            <div id="title">
                <h3>{{visitor.ip_addr}}</h3> visited at <h4>{{visitor.timestamp}}</h4>
            </div>
            <div id="linkś">
                <a target="_blank" v-bind:href="'https://viewdns.info/iplocation/?ip='+visitor.ip_addr">Locate</a>
                <a target="_blank" v-bind:href="'https://viewdns.info/whois/?domain='+visitor.ip_addr">Who Is</a>
                <a target="_blank" v-bind:href="'https://viewdns.info/reversedns/?ip='+visitor.ip_addr">Reverse DNS</a>
                <a target="_blank" v-bind:href="'https://viewdns.info/portscan/?host='+visitor.ip_addr">Port Scan</a>
            </div>
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
        created(){
            axios.get('/backend/visitors')
                    .then(response => { this.visitors = response.data; })
        }
    }
</script>

<style scoped>
    #guest{
        margin-bottom: 10px;
        width: 80%;
        margin-left: 10%;
        padding: 5px;
    }
    #title{
        display: grid;
        grid-template-columns: auto auto auto;
    }
    #linkś{
        margin-top: 5px;
        display: grid;
        grid-template-columns: auto auto auto auto ;
    }
</style>