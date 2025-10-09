import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Document} from "../model/document";
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule} from "@angular/material/icon";

@Component({
  selector: 'app-pdf-list',
  standalone: true,
  imports: [CommonModule,
    MatButtonModule,
    MatIconModule],
  templateUrl: './pdf-list.component.html',
  styleUrl: './pdf-list.component.css'
})
export class PdfListComponent implements OnInit {

  constructor(private http: HttpClient) { }

  documentList: Document[] = [];


  ngOnInit(): void {
    this.getDocumentList();
  }


  getDocumentList() {
    this.http.get<Document[]>("http://localhost:8081/documents/list")
      .subscribe({
        next: (data) => {
          this.documentList = data;
        },
        error: (err) => {
          console.error('Fehler beim Laden der Dokumente', err);
        }
      });
  }

  downloadPdf(id: number): void {
    this.http.get(`http://localhost:8081/documents/download/${id}`, {
      responseType: 'blob',
    }).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);

        const a = document.createElement('a');
        a.href = url;
        a.download = `${name}`;
        a.click();

        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('Fehler beim Download', err);
      }
    });
  }

}
