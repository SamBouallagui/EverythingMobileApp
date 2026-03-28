package com.example.everything;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Adapter pour afficher les commentaires dans RecyclerView
// Gère l'affichage des boutons modifier/supprimer selon les permissions de l'utilisateur
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private Context context;
    private List<Comment> commentList;
    private String currentUserId;
    private String currentUserRole;
    
    // Interface pour gérer les clics sur modifier/supprimer
    public interface OnCommentClickListener {
        void onEditClick(Comment comment, int position);
        void onDeleteClick(Comment comment, int position);
    }
    private OnCommentClickListener listener;

    // Constructeur pour initialiser l'adapter avec toutes les données requises
    public CommentAdapter(Context context, List<Comment> commentList, String currentUserId, String currentUserRole, OnCommentClickListener listener) {
        this.context = context;
        this.commentList = commentList;
        this.currentUserId = currentUserId;
        this.currentUserRole = currentUserRole;
        this.listener = listener;
    }

    // Créer un nouveau ViewHolder pour l'élément commentaire
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    // Lier les données au ViewHolder à la position donnée
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        
        // Définir les infos de base du commentaire
        holder.tvAuthorName.setText(comment.getAuthorName());
        holder.tvContent.setText(comment.getContent());
        holder.tvTimeAgo.setText(comment.getTimeAgo());

        // Afficher un badge spécial pour les modérateurs et admins 
        String role = comment.getAuthorRole();
        if (role != null && (role.equals("moderator") || role.equals("admin"))) {
            holder.tvRoleBadge.setVisibility(View.VISIBLE);
            holder.tvRoleBadge.setText(role.equals("admin") ? "ADMIN" : "MOD");
            // Couleurs différentes pour admin vs mod
            holder.tvRoleBadge.setBackgroundColor(role.equals("admin") ? Color.parseColor("#FF6B35") : Color.parseColor("#7B61FF"));
        } else {
            holder.tvRoleBadge.setVisibility(View.GONE);
        }

        // Vérifier si l'utilisateur actuel peut modifier ou supprimer ce commentaire
        // Les utilisateurs peuvent modifier leurs propres commentaires, les mods/admins peuvent gérer les commentaires des membres
        boolean isAuthor = currentUserId.equals(comment.getAuthorId());
        String commentAuthorRole = comment.getAuthorRole();
        boolean canManageAsMod = ("moderator".equals(currentUserRole) || "admin".equals(currentUserRole)) 
                                && (commentAuthorRole != null && "member".equals(commentAuthorRole)) 
                                && !isAuthor;

        holder.btnEdit.setVisibility((isAuthor || canManageAsMod) ? View.VISIBLE : View.GONE);
        holder.btnDelete.setVisibility((isAuthor || canManageAsMod) ? View.VISIBLE : View.GONE);

        // Gérer le clic sur le bouton modifier - seulement si l'utilisateur a la permission
        holder.btnEdit.setOnClickListener(v -> {
            if (isAuthor || canManageAsMod) {
                listener.onEditClick(comment, holder.getAdapterPosition());
            }
        });

        // Gérer le clic sur le bouton supprimer - seulement si l'utilisateur a la permission
        holder.btnDelete.setOnClickListener(v -> {
            if (isAuthor || canManageAsMod) {
                listener.onDeleteClick(comment, holder.getAdapterPosition());
            }
        });
    }

    // Retourner le nombre total de commentaires
    @Override
    public int getItemCount() {
        return commentList.size();
    }

    // Mettre à jour un seul commentaire quand il est modifié
    public void updateItem(int position) {
        if (position >= 0 && position < commentList.size()) {
            notifyItemChanged(position);
        }
    }

    // Classe ViewHolder pour contenir les vues des éléments de commentaire
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthorName, tvContent, tvTimeAgo, tvRoleBadge;
        View btnEdit, btnDelete;

        // Trouver toutes les vues pour l'élément de commentaire
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthorName = itemView.findViewById(R.id.tvCommentAuthor);
            tvContent = itemView.findViewById(R.id.tvCommentContent);
            tvTimeAgo = itemView.findViewById(R.id.tvCommentTime);
            tvRoleBadge = itemView.findViewById(R.id.tvCommentRoleBadge);
            btnEdit = itemView.findViewById(R.id.btnEditComment);
            btnDelete = itemView.findViewById(R.id.btnDeleteComment);
        }
    }
}
