import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

# Coordinate conversion function
def calculate_point(azimuth_deg, elevation_deg, radius):
    azimuth_rad = np.radians(azimuth_deg)
    elevation_rad = np.radians(elevation_deg)
    x = radius * np.cos(elevation_rad) * np.cos(azimuth_rad)
    z = radius * np.cos(elevation_rad) * np.sin(azimuth_rad)
    y = radius * np.sin(elevation_rad)
    return x, y, z

# Parameters
azimuth = 30  # Azimuth angle
elevation = 60  # Elevation angle
radius = 1  # Radius

# Coordinate calculation
x, y, z = calculate_point(azimuth, elevation, radius)

# Plotting
fig = plt.figure(figsize=(8, 6))
ax = fig.add_subplot(111, projection='3d')

# Draw sphere
u = np.linspace(0, 2 * np.pi, 8)
v = np.linspace(0, np.pi, 8)
xs = radius * np.outer(np.cos(u), np.sin(v))
ys = radius * np.outer(np.sin(u), np.sin(v))
zs = radius * np.outer(np.ones(np.size(u)), np.cos(v))
ax.plot_wireframe(xs, -ys, zs, color='lightgray', alpha=0.2)

# Axes
ax.quiver(0, 0, 0, 1.2, 0, 0, color='r', arrow_length_ratio=0.1)
ax.text(1.3, 0, 0, '+X (Right)', color='r')
ax.quiver(0, 0, 0, 0, -1.2, 0, color='g', arrow_length_ratio=0.1)
ax.text(0, -1.3, 0, '-Z (Depth)', color='g')
ax.quiver(0, 0, 0, 0, 0, 1.2, color='b', arrow_length_ratio=0.1)
ax.text(0, 0, 1.3, '-Y (Height)', color='b')

# Point and line
ax.scatter([x], [-z], [y], color='purple', s=100, label='Converted Point')
ax.plot([0, x], [0, -z], [0, y], color='purple', linestyle='--')

# Azimuth arc
az_arc = np.linspace(0, np.radians(azimuth), 100)
az_arc_x = +radius * np.cos(az_arc)
az_arc_y = -radius * np.sin(az_arc)  # Y軸は下が正
az_arc_z = np.zeros_like(az_arc)
ax.plot(az_arc_x, az_arc_y, az_arc_z, color='orange', label='Azimuth')

# Elevation arc
el_arc = np.linspace(0, np.radians(elevation), 100)
el_arc_z = radius * np.sin(el_arc)  # Negative because Y-axis is down
el_arc_r = radius * np.cos(el_arc)
el_arc_x = +el_arc_r * np.cos(np.radians(azimuth))
el_arc_y = -el_arc_r * np.sin(np.radians(azimuth))
ax.plot(el_arc_x, el_arc_y, el_arc_z, color='cyan', label='Elevation')

# Labels
ax.set_xlabel('X (Right)')
ax.set_ylabel('Y (Down)')
ax.set_zlabel('Z (Depth)')
ax.set_title('Polar to Cartesian Conversion\n(X: Right, Y: Down, Z: Depth)')
ax.legend()

plt.show()
