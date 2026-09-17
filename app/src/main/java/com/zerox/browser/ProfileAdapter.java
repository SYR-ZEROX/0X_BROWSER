package com.zerox.browser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    public interface OnProfileAction {
        void onAction(String profileName);
    }

    private final Context context;
    private final List<String> profileNames;
    private final String activeName;
    private final OnProfileAction onRun;
    private final OnProfileAction onEdit;
    private final OnProfileAction onDelete;

    public ProfileAdapter(Context context, List<String> profileNames, String activeName,
                          OnProfileAction onRun, OnProfileAction onEdit, OnProfileAction onDelete) {
        this.context = context;
        this.profileNames = profileNames;
        this.activeName = activeName;
        this.onRun = onRun;
        this.onEdit = onEdit;
        this.onDelete = onDelete;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String profileName = profileNames.get(position);
        Profile profile = ProfileManager.getProfile(context, profileName);

        // اسم البروفايل (مع علامة نشط)
        String prefix = profileName.equals(activeName) ? "✅ " : "⬜ ";
        holder.profileName.setText(prefix + profileName);

        // البروكسي
        if (profile.proxy == null || profile.proxy.isEmpty()) {
            holder.profileProxy.setText("🌐 غير محدد");
        } else {
            String proxyDisplay = profile.proxy;
            if (proxyDisplay.contains("@")) {
                String[] parts = proxyDisplay.split("@");
                proxyDisplay = "***@" + parts[1];
            }
            holder.profileProxy.setText("🌐 " + proxyDisplay);
        }

        // الموقع
        if (profile.city != null && !profile.city.isEmpty()) {
            holder.profileLocation.setText("📍 " + profile.city + ", " + profile.countryName);
        } else {
            holder.profileLocation.setText("📍 " + profile.latitude + ", " + profile.longitude);
        }

        // الأزرار
        holder.runBtn.setOnClickListener(v -> onRun.onAction(profileName));
        holder.editBtn.setOnClickListener(v -> onEdit.onAction(profileName));
        holder.deleteBtn.setOnClickListener(v -> onDelete.onAction(profileName));
    }

    @Override
    public int getItemCount() {
        return profileNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView profileName, profileProxy, profileLocation;
        Button runBtn, editBtn, deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileName = itemView.findViewById(R.id.profileName);
            profileProxy = itemView.findViewById(R.id.profileProxy);
            profileLocation = itemView.findViewById(R.id.profileLocation);
            runBtn = itemView.findViewById(R.id.runBtn);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}