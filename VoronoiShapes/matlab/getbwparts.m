function getbwparts( file_name, threshold)
%GETBWOBJECTS Exports the pixels from the input file that belong to individual objects.
% Simple objects are detected using bwconncomp of the BW image. 
% Closing is performed to remove noise pixels 

    dirName = strcat('C:\Voronoi\',file_name);
    mkdir(dirName);
    
    im_in = imread(file_name);
    im_in = imresize(im_in, [max(size(im_in)), max(size(im_in))]);
    im_in = imgaussfilt(im_in);
    
    BW = imcomplement(im2bw(im_in, threshold));
    
    se = strel('square',3);
    BW = imclose(BW,se);
   
    cc = bwconncomp(BW)
    labeled = labelmatrix(cc);
    RGB_label = label2rgb(labeled, @hsv, 'w', 'shuffle');
    imshow(RGB_label,'InitialMagnification','fit')
    
    shapes = cc.PixelIdxList;
    dims = size(im_in);
    dimsFN = [dirName '/dims.txt'];
    dfid = fopen(dimsFN, 'wt');
    fprintf(dfid,'%d,%d\n',dims(1),dims(2));
    fclose(dfid);
    for ii = 1 : cc.NumObjects
        pixels = shapes{ii};
        name = sprintf('%s/shape%d.txt',dirName,ii);
        fid = fopen(name, 'wt');
        for jj = 1 : length(pixels)
            [X,Y] = ind2sub(dims,pixels(jj));
            R = im_in(X,Y,1);
            G = im_in(X,Y,2);
            B = im_in(X,Y,3);
            fprintf(fid,'%d,%d,%d,%d,%d\n',X,Y,R,G,B);
        end
        fclose(fid);
    end
end

