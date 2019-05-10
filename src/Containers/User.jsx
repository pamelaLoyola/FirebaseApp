import React, { Component } from 'react';

import { withStyles } from '@material-ui/core/styles';

import firebase from '../Initializers/firebase';

import Header from '../Components/Header';

class User extends Component {
  constructor(props){
      super(props)
      this.state={
          user: false,
          userURL: '',
          userName: ''
      }
  }

  componentWillMount(){
    firebase.auth().onAuthStateChanged(user => {
        if(user){
          console.log(user.providerData[0])
            //Si hay sesión
            this.setState({
                user:true,
                userURL: user.providerData[0].photoURL,
                userName: user.providerData[0].displayName
            })
        }else{
            //Si no hay sesión
            this.setState({
                user:false,
                userURL: ''
            })
        }
    })
}

  login(){
    let provider = new firebase.auth.GoogleAuthProvider();//una nueva instacia de google
    firebase.auth().signInWithPopup(provider)//el metodo auth() llamamos el submetodo de ventana pasandole a google
    .then(result => {//esto es una promesa y que me retornará un resultado
        console.log(result)
    })
    .catch(err => console.log(err))//si el usuario no permite su autenticación
  }

  logout(){
    firebase.auth().signOut()
}

  render() {
    return (
      <Header 
      onDrawerToggle={this.props.onDrawerToggle}
      login={this.login}
      logout={this.logout}
      user={this.state.user}
      name={this.state.userName}
      photo={this.state.userURL}/>
    )
  }
}

export default withStyles({})(User);
