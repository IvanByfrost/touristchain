<?php
namespace App\Controllers;
use App\Models\Establishment;

class EstablishmentsController extends BaseController {
    public function index(): void {
        $items = Establishment::all();
        $this->render('establishments/index', ['items'=>$items]);
    }
    public function show($id): void {
        $item = Establishment::find((int)$id);
        if (!$item) { $this->setFlash('error','Establecimiento no encontrado'); $this->redirect('/establishments'); }
        $this->render('establishments/show', ['item'=>$item]);
    }
    public function create(): void {
        if ($this->isPost()) {
            if (trim((string)$this->input('name'))==='') { $this->setFlash('error','Nombre requerido'); $this->back(); return; }
            Establishment::create(['name'=>$this->input('name'), 'address'=>$this->input('address')]);
            $this->setFlash('success','Establecimiento creado'); $this->redirect('/establishments');
        }
        $this->render('establishments/create');
    }
    public function update($id): void {
        $item = Establishment::find((int)$id);
        if (!$item) { $this->setFlash('error','Establecimiento no encontrado'); $this->redirect('/establishments'); }
        if ($this->isPost()) {
            if (trim((string)$this->input('name'))==='') { $this->setFlash('error','Nombre requerido'); $this->back(); return; }
            Establishment::update((int)$id, ['name'=>$this->input('name'), 'address'=>$this->input('address')]);
            $this->setFlash('success','Establecimiento actualizado'); $this->redirect('/establishments');
        }
        $this->render('establishments/edit', ['item'=>$item]);
    }
    public function delete($id): void {
        Establishment::delete((int)$id);
        $this->setFlash('success','Establecimiento eliminado');
        $this->redirect('/establishments');
    }
}
