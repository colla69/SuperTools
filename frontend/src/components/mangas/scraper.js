import axios from 'axios'

let doStuff = function (){
    axios.get("/mangas/one-piece")
        .then(response => {
            const html = response.data;
            return html;
        });
}
doStuff();