

<template>
    <v-card
            id="app"
            raised
    >
        <a :href="link" target="_blank">
            <v-img eager

                   :src="imglink"
                   aspect-ratio="1"
            >
                <div class="fill-height bottom-gradient t ">
                    <h3> {{name}} </h3>
                </div>
            </v-img>
        </a>
    </v-card>
</template>

<script>
    export default {
        name: "AppCard",
        props: {
            link: String,
            imglink: String,
            name: String
        }
    }
</script>

<style scoped>
    h3{
        width: 100%;
        margin-top: 90px;
    }
    #app{
        height: 120px;
        width: 120px;
        margin: 7px
    }
    .t{
        color: white;
        display: flex;
    }
    .bottom-gradient {
        background-image: linear-gradient(to top, rgba(0, 0, 0, 0.5) 0%, transparent 80px);
    }
    .img{
        max-width: 100px;
        max-height:100px;
        min-width: 100px;
        min-height:100px;
        box-sizing: border-box;
        border-radius: 15px;
        -moz-border-radius: 15px;
        -webkit-border-radius: 15px;
        border: 1px none  #000000;
    }
    .app {
        padding: 0px;
        margin: 7px;
        background-color: lightblue;
        opacity: .8;
        display: inline-block;

        height: 125px;
        min-height: 125px;
        max-height: 125px;
        max-width: 100px;
        min-width: 100px;

        box-sizing: border-box;
        border-radius: 15px;
        -moz-border-radius: 15px;
        -webkit-border-radius: 15px;
        border: 1px none  #000000;

        -webkit-box-shadow: 0px 1px 15px 2px rgba(0, 0, 0, 0.45);
        -moz-box-shadow: 0px 1px 15px 2px rgba(0, 0, 0, 0.45);
        box-shadow: 0px 1px 15px 2px rgba(0, 0, 0, 0.45);
    }

</style>