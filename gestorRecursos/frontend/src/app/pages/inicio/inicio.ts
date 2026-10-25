import { Component } from '@angular/core';
import { RouterLink } from '@angular/router'; // 1. Importamos la directiva de rutas

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [RouterLink], // 2. La agregamos al arreglo de imports
  templateUrl: './inicio.html',
  styleUrl: './inicio.css'
})
export class InicioComponent {
}