import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Document} from "../model/document";
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule} from "@angular/material/icon";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-pdf-list',
  standalone: true,
  imports: [FormsModule,
    CommonModule,
    MatButtonModule,
    MatIconModule],
  templateUrl: './pdf-list.component.html',
  styleUrl: './pdf-list.component.css'
})
export class PdfListComponent implements OnInit {

  constructor(private http: HttpClient) { }

  documentList: Document[] = [];
  searchQuery: string = "";
  currentSelectedPdf: number | null = null;

  ngOnInit(): void {
    this.getDocumentList();
  }

  toggleSummary(doc: Document): void {
    if (this.currentSelectedPdf === doc.id) {
      this.currentSelectedPdf= null; // Zuklappen, wenn bereits offen
    } else {
      this.currentSelectedPdf = doc.id;
    }
  }
  searchDocuments() {
    if (!this.searchQuery || this.searchQuery.trim() === '') {
      this.getDocumentList(); // Wenn leer, lade alle
    } else {
      this.http.get<Document[]>(`http://localhost:8081/documents/search?q=${this.searchQuery}`)
        .subscribe({
          next: (data) => {
            this.documentList = data;
          },
          error: (err) => {
            console.error('Fehler bei der Suche', err);
          }
        });
    }
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

  deleteDocument(id: number): void {
    this.http.delete(`http://localhost:8081/documents/${id}`)
      .subscribe({
        next: () => {
          this.documentList = this.documentList.filter(doc => doc.id !== id);
        },
        error: (err) => {
          console.error('Fehler beim Löschen', err);
        }
      });
  }

}
