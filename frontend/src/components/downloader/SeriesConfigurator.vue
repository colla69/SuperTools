<template>
    <div style="width: 100%">
        <v-data-table
                :headers="headers"
                v-model="state.selected"
                :items="state.series"
                item-key="id"
                show-select
                class="elevation-5"
                :footer-props="{
                      showFirstLastPage: true,
                      firstIcon: 'mdi-arrow-collapse-left',
                      lastIcon: 'mdi-arrow-collapse-right',
                      prevIcon: 'mdi-minus',
                      nextIcon: 'mdi-plus',
                      itemsPerPageOptions: [50,100,-1],
                    }"
        >
            <template v-slot:top>
                <v-toolbar flat color="white">
                    <v-toolbar-title>Configuration Table</v-toolbar-title>
                    <v-divider
                            class="mx-4"
                            inset
                            vertical
                    ></v-divider>
                    <v-spacer></v-spacer>
                    <v-dialog v-model="state.dialog" max-width="500px">
                        <template v-slot:activator="{ on }">
                            <v-btn color="primary" dark class="mb-2" v-on="on">New Item</v-btn>
                            <v-btn color="secondary" dark @click="toggleActive" class="mb-2" style="margin-right: 5px; " >Toggle Active</v-btn>
                        </template>
                        <v-card>

                            <v-card-text>
                                <v-container>
                                    <v-row>
                                        <v-col cols="12" sm="6" md="4">
                                            <v-text-field outlined clearable
                                                          type="text"
                                                          v-model="state.editedItem.label" label="Name"></v-text-field>
                                        </v-col>
                                        <v-col cols="12" sm="6" md="4">
                                            <v-text-field outlined clearable
                                                          type="text"
                                                          v-model="state.editedItem.linkpart" label="Partial Link"></v-text-field>
                                        </v-col>
                                        <v-col cols="12" sm="6" md="4">
                                            <v-text-field outlined
                                                          max="50"
                                                          min="1"
                                                          step="1"
                                                          type="number"
                                                          v-model="state.editedItem.startSeriesNo" label="First Serie"></v-text-field>
                                        </v-col>
                                        <v-col cols="12" sm="6" md="4">
                                            <v-text-field outlined
                                                          max="50"
                                                          min="1"
                                                          step="1"
                                                          type="number"
                                                          v-model="state.editedItem.endSeriesNo" label="Last Serie"></v-text-field>
                                        </v-col>
                                        <v-col cols="12" sm="6" md="4">
                                                <v-switch flat inset label="Active" v-model="state.editedItem.active"></v-switch>
                                        </v-col>
                                    </v-row>
                                </v-container>
                            </v-card-text>

                            <v-card-actions>
                                <v-spacer></v-spacer>
                                <v-btn color="secondary" class="mb-2" @click="cancel">
                                    <v-icon dark right>mdi-cancel</v-icon>
                                    Cancel
                                </v-btn>
                                <v-btn color="primary" class="mb-2" @click="save" >
                                    <v-icon dark right>mdi-checkbox-marked-circle</v-icon>
                                    Save
                                </v-btn>
                            </v-card-actions>
                        </v-card>
                    </v-dialog>
                </v-toolbar>
            </template>


            <template v-slot:item.active="{ item }" >
                <v-switch flat inset disabled v-model="item.active"></v-switch>
            </template>

            <template v-slot:item.action="{ item }">

                <v-icon small
                        @click="editItem(item)"
                >
                    mdi-pencil
                </v-icon>
                <v-icon small
                        @click="deleteItem(item)"
                >
                    mdi-delete-empty
                </v-icon>
            </template>
        </v-data-table>
    </div>
</template>

<script>
    import { reactive } from "@vue/composition-api";
    import axios from "axios";

    export default {
        name: "SeriesConfigurator",
        setup() {
            let { state, headers, editItem, deleteItem, save, cancel, toggleActive } = useSeriesConf();
            return { state, headers, editItem, deleteItem, save, cancel, toggleActive };
        },
        methods: {
            methodThatForcesUpdate() {
                this.$forceUpdate();
            }
        }
    }

    function useSeriesConf() {

        let state = reactive({
            series: [],
            selected: [],
            dialog: false,
            editedItem: {
                label: '',
                linkpart: '',
                startSeriesNo: '',
                endSeriesNo: '',
                active: false,
            },
            defaultItem: {
                label: '',
                linkpart: '',
                startSeriesNo: '',
                endSeriesNo: '',
                active: false,
            },
        });
        let headers = reactive( [
                {
                    text: 'active',
                    value: 'active'
                },
                {
                    text: 'Name',
                    value: 'label'
                },
                {text: 'linkpart', value: 'linkpart'},
                {text: 'start', value: 'startSeriesNo'},
                {text: 'end', value: 'endSeriesNo'},
                {text: 'Actions', value: 'action', sortable: false },
            ] )
        ;

        axios.get('/backend/seriesConfig')
            .then( response => {
                state.series = response.data
            } );

        function editItem(item) {
            state.editedItem =  item;
            state.dialog = true;
        }

        function deleteItem(item){
            const index = state.series.indexOf(item);
            confirm('Are you sure you want to delete this item?' +item)  && state.series.splice(index, 1)
            axios.post('/backend/deleteSeriesConfig',
                item
            );
        }

        function save(){
            // axios post
            if (state.series.indexOf(state.editedItem) == -1){
                state.series.push(state.editedItem);
            }
            axios.post('/backend/saveSeriesConfig',
                state.series
            );
            state.dialog = false;
            state.editedItem = state.defaultItem;
        }

        function cancel(){
            state.editedItem = state.defaultItem;
            state.dialog = false;
        }

        function toggleActive(){
            // save selection with turned active
            // clear selection
            state.selected = [];
            return true;
        }

        return { state, headers, editItem, deleteItem, save, cancel, toggleActive }
    }
</script>

<style scoped>

    table.v-table v-row tbody td, table.v-table tbody th {
        height: 19px;
    }
</style>