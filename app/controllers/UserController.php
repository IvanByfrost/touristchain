<?php
namespace V01\Touristchain\Controllers;
use V01\Touristchain\Models\User;

class UsersController extends BaseController {
    public function index(): void {
        $users = User::all();
        $this->render('users/index', ['users'=>$users]);
    }
    public function show($id): void {
        $user = User::find((int)$id);
        if (!$user) { $this->setFlash('error','Usuario no encontrado'); $this->redirect('/users'); }
        $this->render('users/show', ['user'=>$user]);
    }
    public function create(): void {
        if ($this->isPost()) {
            if (trim((string)$this->input('name'))==='') { $this->setFlash('error','Nombre requerido'); $this->back(); return; }
            User::create(['name'=>$this->input('name'), 'email'=>$this->input('email')]);
            $this->setFlash('success','Usuario creado'); $this->redirect('/users');
        }
        $this->render('users/create');
    }
    public function update($id): void {
        $user = User::find((int)$id);
        if (!$user) { $this->setFlash('error','Usuario no encontrado'); $this->redirect('/users'); }
        if ($this->isPost()) {
            if (trim((string)$this->input('name'))==='') { $this->setFlash('error','Nombre requerido'); $this->back(); return; }
            User::update((int)$id, ['name'=>$this->input('name'), 'email'=>$this->input('email')]);
            $this->setFlash('success','Usuario actualizado'); $this->redirect('/users');
        }
        $this->render('users/edit', ['user'=>$user]);
    }
    public function delete($id): void {
        User::delete((int)$id);
        $this->setFlash('success','Usuario eliminado');
        $this->redirect('/users');
    }
}
