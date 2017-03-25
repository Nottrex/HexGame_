package game.map;

import java.util.Random;

public class ValueNoise_2D {
	
	private int width;

	private int wantedHeight;
	private int wantedWidth;
	
	private float[][] heightMap;
	
	private int octaves = 4;
	private int startFrequenzyX = 3;
	private int startFrequenzyY = 3;
	private float alpha = 20;

	public ValueNoise_2D(int w, int h){
		width = Math.max(w, h);
		
		wantedHeight = h;
		wantedWidth = w;
		
		heightMap = new float[width][width];
	}
	
	public void calculate(){
		
		Random r = new Random();

		int currentFrequenzyX = startFrequenzyX;
		int currentFrequenzyY = startFrequenzyY;
		float currentAlpha = alpha;

		for(int oc = 0; oc < octaves; oc++){

            float[][] diskretPoint = null;
			diskretPoint = new float[currentFrequenzyX + 1][currentFrequenzyY + 1];
			for(int x = 0; x < currentFrequenzyX + 1; x++){
				for(int y = 0; y < currentFrequenzyY + 1; y++){
					diskretPoint[x][y] = r.nextFloat() * (r.nextInt((int)(2 * currentAlpha)) - currentAlpha);
				}
			}

			for(int x = 0; x < width; x++) {
				for (int y = 0; y < width; y++) {
					float currentX = x / (float) width * currentFrequenzyX;
					float currentY = y / (float) width * currentFrequenzyY;
					int indexX = (int) currentX;
					int indexY = (int) currentY;

					float w;
					float w0 = interpolate(diskretPoint[indexX][indexY], diskretPoint[indexX + 1][indexY], currentX - indexX);
					float w1 = interpolate(diskretPoint[indexX][indexY + 1], diskretPoint[indexX + 1][indexY + 1], currentX - indexX);
					w = interpolate(w0, w1, currentY - indexY);

					heightMap[x][y] += w;
				}
			}

			currentFrequenzyX *= 2;
			currentFrequenzyY *= 2;
			alpha /= 2;
		}
		normalize();
	}

	private void normalize(){
		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;
		for(int x = 0; x < width; x++){
			for(int y = 0; y < width; y++){
				if(heightMap[x][y] < min) min = heightMap[x][y];
				if(heightMap[x][y] > max) max = heightMap[x][y];
			}
		}

		max -= min;
		
		for(int x = 0; x < width; x++){
			for(int y = 0; y < width; y++){
				heightMap[x][y] -= min;
				heightMap[x][y] /= max;
			}
		}
	}
	
	private float interpolate(float a, float b, float t){
		double ft = (1 - Math.cos(t * Math.PI))/2;
		return (float) (a * (1 - ft) + b * ft);
	}

	public float[][] getHeightMap() {
		if(width == wantedWidth && width == wantedHeight)return heightMap;
		else{ 
			float[][] realMap = new float[wantedWidth][wantedHeight];
			for(int x = 0; x < wantedWidth; x++){
				for(int y = 0; y < wantedHeight; y++){
					realMap[x][y] = heightMap[x][y];
				}
			}
			
			return realMap;
		}
	}

	public void setOctaves(int octaves) {
		this.octaves = octaves;
	}

	public void setStartFrequenzyX(int startFrequenzyX) {
		this.startFrequenzyX = startFrequenzyX;
	}

	public void setStartFrequenzyY(int startFrequenzyY) {
		this.startFrequenzyY = startFrequenzyY;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
}